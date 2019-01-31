/*
 * Copyright 2019 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chaos.fx.cnbeta.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtil {

    public static String encryptToMD5(String info) {
        byte[] digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(info.getBytes(StandardCharsets.UTF_8));
            digest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byte2hex(digest);
    }

    public static String encryptToSHA(String info) {
        byte[] digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(info.getBytes(StandardCharsets.UTF_8));
            digest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byte2hex(digest);
    }

    private static String byte2hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder s = new StringBuilder();
        for (byte b : bytes) {
            String tmp = Integer.toHexString(b & 0xff);
            if (tmp.length() == 1) {
                s.append("0").append(tmp);
            } else {
                s.append(tmp);
            }
        }
        return s.toString().toUpperCase();
    }
}
