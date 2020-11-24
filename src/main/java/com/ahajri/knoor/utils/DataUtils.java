package com.ahajri.knoor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

public final class DataUtils {

    public static String clobToString(Clob data) {
        long dataL = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            Reader reader = null;
            BufferedReader br = null;
            try {
                reader = data.getCharacterStream();
                br = new BufferedReader(reader);
                String line;
                while (null != (line = br.readLine())) {
                    sb.append(line);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    reader.close();
                } catch (IOException e) {
                   //
                }

            }
        }
        return sb.toString();
    }
}
