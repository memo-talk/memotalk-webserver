package com.memotalk.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogData {
    private String level;
    private String message;
    private long timeStamp;
    // 여기에 필요한 다른 필드들을 추가할 수 있습니다.

    public LogData(ILoggingEvent event) {
        this.level = event.getLevel().toString();
        this.message = event.getFormattedMessage();
        this.timeStamp = event.getTimeStamp();
        // 여기에 필요한 다른 필드들을 설정할 수 있습니다.
    }

    // getter와 setter 메소드는 생략됨
}