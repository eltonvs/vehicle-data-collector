package br.ufrn.imd.vdc.domain;

import java.io.Serializable;

/**
 * Created by Cephas on 27/03/2017.
 */

public class Person implements Serializable {

    public static String[] COLUNAS = new String[]{"ID", "NAME", "PASSWORD"};

    private int id;
    private String name;
    private String password;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {

        return "id:" + id + ";" +
                "name:" + name + ";" +
                "password:" + password + ";";
    }
}
