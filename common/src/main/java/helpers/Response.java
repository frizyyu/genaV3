package helpers;

import java.io.Serializable;

public record Response(String textCommand, String[] args) implements Serializable {
}
