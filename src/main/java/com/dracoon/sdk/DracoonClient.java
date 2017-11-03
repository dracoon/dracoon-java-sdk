package com.dracoon.sdk;

public abstract class DracoonClient {

    public interface Server {

    }

    public interface Account {

    }

    public interface Users {

    }

    public interface Groups {

    }

    public interface Roles {

    }

    public interface Permissions {

    }

    public interface Nodes {

    }

    public interface Shares {

    }

    public interface Events {

    }

    public abstract Server server();
    public abstract Account account();
    public abstract Users users();
    public abstract Groups groups();
    public abstract Roles roles();
    public abstract Permissions permissions();
    public abstract Nodes nodes();
    public abstract Shares shares();
    public abstract Events events();

}
