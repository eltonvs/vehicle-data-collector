package br.ufrn.imd.vdc.io;

/**
 * Created by elton on 12/05/17.
 */

public abstract class CommandTask {
    private final ICommand command;
    private Long id;
    private CommandTaskState state;

    public CommandTask(ICommand command) {
        this.command = command;
        this.state = CommandTaskState.NEW;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ICommand getCommand() {
        return command;
    }

    public CommandTaskState getState() {
        return state;
    }

    public void setState(CommandTaskState state) {
        this.state = state;
    }

    public enum CommandTaskState {
        NEW,
        RUNNING,
        FINISHED,
        EXECUTION_ERROR,
        BROKEN_PIPE,
        QUEUE_ERROR,
        NOT_SUPPORTED
    }
}
