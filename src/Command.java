public enum Command {
    STOP("/stop"),
    CREATE("/create"),
    CONNECT("/connect"),
    CHANGE_NAME("/change");

    String commandName;

    Command (String commandName) {
        this.commandName = commandName;
    }
}
