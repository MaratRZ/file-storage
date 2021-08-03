package netty;

import message.AbstractMessage;

public interface CallBack {
    void call(AbstractMessage message);
}
