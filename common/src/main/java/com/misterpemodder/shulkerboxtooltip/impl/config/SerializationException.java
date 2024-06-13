package com.misterpemodder.shulkerboxtooltip.impl.config;

import java.io.Serial;

public class SerializationException extends Exception {
  @Serial
  private static final long serialVersionUID = 1L;

  public SerializationException(Throwable cause) {
    super(cause);
  }

  public SerializationException(String message) {
    super(message);
  }
}
