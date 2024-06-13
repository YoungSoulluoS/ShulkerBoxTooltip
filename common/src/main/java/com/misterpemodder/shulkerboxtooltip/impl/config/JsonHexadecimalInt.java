package com.misterpemodder.shulkerboxtooltip.impl.config;

import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.JsonPrimitive;

/**
 * Helper class to write hex ints to the JSON output, for serialization uses only.
 */
public class JsonHexadecimalInt extends JsonPrimitive {
  JsonHexadecimalInt(int value) {
    super(value);
  }

  @Override
  public String toJson(JsonGrammar grammar, int depth) {
    return String.format("%#x", ((Number) this.getValue()).intValue());
  }
}
