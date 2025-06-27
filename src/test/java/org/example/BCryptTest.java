package org.example;

import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.Test;

public class BCryptTest {

    @Test
    public void testBCrypt() {
        String password = "123456";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashedPassword);
        System.out.println(BCrypt.checkpw(password, hashedPassword));
    }
}
