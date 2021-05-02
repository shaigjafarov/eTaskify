package az.etaskify.service;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
 public  class PasswordServiceImpl implements  PasswordService {

    private static BCryptPasswordEncoder passwordEcorder = new BCryptPasswordEncoder();


    public String bcryptEncryptor(String plainText) {
        return passwordEcorder.encode(plainText);
    }

    public Boolean doPasswordsMatch(String rawPassword,String encodedPassword) {
        return passwordEcorder.matches(rawPassword, encodedPassword);
    }
}