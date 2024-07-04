package helpers;

import java.io.Serializable;

public record Request(byte[] voiceCommand) implements Serializable {
}
