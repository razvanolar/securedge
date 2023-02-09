package com.securedge.server.service.impl;

import com.securedge.server.model.User;
import com.securedge.server.repository.AccountRepository;
import com.securedge.server.repository.UserRepository;
import com.securedge.server.service.UserService;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${app.emailValidationRegEx}")
    private String emailValidationRegEx;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final MessageDigest messageDigest;

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository) throws NoSuchAlgorithmException {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;

        messageDigest = MessageDigest.getInstance("MD5");
    }

    @Override
    public List<User> findAll() {
        logger.info("Retrieving all users...");
        return userRepository.findAll();
    }

    @Override
    public List<User> findAll(final String sortOption) {
        logger.info("Retrieving all users...");
        List<User> users = userRepository.findAll();
        logger.info("Sorting result by name " + sortOption);
        users.sort((user1, user2) -> {
            if (sortOption.equals("ASC")) {
                return user1.getName().compareTo(user2.getName());
            } else {
                return user2.getName().compareTo(user1.getName());
            }
        });
        return users;
    }

    @Override
    public User findById(int id) {
        logger.info("Find user by id: " + id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User create(User user) {
        validate(user);

        user.setId(null);
        user.setPassword(encodePassword(user.getPassword()));
        user.setCreatedDate(new Timestamp(System.currentTimeMillis()));

        logger.info("Creating new user");
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        logger.info("Validating update user request");
        if (Objects.isNull(user.getId())) {
            logger.error("User id not specified");
            throw new RuntimeException("User id not specified");
        }
        if (StringUtil.isNullOrEmpty(user.getName())) {
            logger.error("Username not provided");
            throw new RuntimeException("Username not provided");
        }

        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isEmpty()) {
            logger.error("Unable to find user by id: " + user.getId());
            throw new RuntimeException("Unable to find user by id: " + user.getId());
        }

        User dbUser = userOptional.get();
        dbUser.setName(user.getName());

        logger.info("Updating user id: " + user.getId());
        return userRepository.save(dbUser);
    }

    @Override
    public void delete(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            logger.error("Unable to find user by specified id: " + id);
            throw new RuntimeException("Unable to find user by specified id: " + id);
        }

        logger.info("Delete user id: " + id);
        userRepository.delete(userOptional.get());
    }

    private String encodePassword(String password) {
        logger.info("Encoding user password");
        messageDigest.update(password.getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger no = new BigInteger(1, digest);

        String hashtext = no.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private void validate(User user) {
        logger.info("Validating user...");
        if (StringUtil.isNullOrEmpty(user.getName())) {
            logger.error("Validation failed! Username not provided");
            throw new RuntimeException("Username not provided");
        }

        if (StringUtil.isNullOrEmpty(user.getPassword())) {
            logger.error("Validation failed! Password not provided");
            throw new RuntimeException("Password not provided");
        }

        String email = user.getEmail();
        if (StringUtil.isNullOrEmpty(email)) {
            logger.error("Validation failed! Email not provided");
            throw new RuntimeException("Email not provided");
        }

        Pattern pattern = Pattern.compile(emailValidationRegEx);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            logger.error("Validation failed! Provided email is not valid: " + email);
            throw new RuntimeException("Provided email is not valid" + email);

        }

        if (userRepository.findFirstByEmail(email).isPresent()) {
            logger.error("Validation failed! The email is already in use: " + email);
            throw new RuntimeException("The email is already in use: " + email);
        }

        if (accountRepository.findById(user.getAccountId()).isEmpty()) {
            logger.error("Validation failed! Specified account id can not be found: " + user.getAccountId());
            throw new RuntimeException("Specified account id can not be found: " + user.getAccountId());
        }
    }
}
