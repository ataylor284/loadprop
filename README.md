loadprop
========

[![Build Status](https://travis-ci.org/ataylor284/loadprop.png?branch=master)](https://travis-ci.org/ataylor284/loadprop)

A better way to handle properties config files in Java 8.

loadprop loads in properties by mapping them to a plain old Java
object.  It uses common conventions to do it cleanly and concisely.
It's a small, simple library with no external dependencies.

The main ideas behind loadprop:
  * To avoid properties file and class from getting out of sync over time:
    * Require 1-to-1 mapping of properties to class fields.
    * Generate errors if there are extra or missing properties.
    * Class members can be marked as optional using Java 8 `Optional`
      class.
  * Automatically convert fields:
    * Convert all primitive types and their wrappers transparently.
    * Use standard conversions provided by JDK.
    * Generate errors if conversion fails.
    * Allow 0x prefix for hexidecimal whole number types, but not 0
      prefix for octal.  Have octal constants ever been used
      intentionally?
  * Report errors consistantly with exceptions, providing as much
    useful information as possible.

# Basic Code Examples

    // Config.java
    class Config {
        public String bindAddr;
        public Optional<Integer> port;
    }

    // config.properties
    bindAddr = 127.0.0.1
    port = 80

    // main program
    PropertiesLoader propertiesLoader = new PropertiesLoader();
    Config config = propertiesLoader.load(Config.class, new FileInputStream("config.properties"));
    System.out.println("port = " + config.port.orElse(8080));

# TODO

* Provide mapping from `name.something.whatever` style properties to
camelCase field names.
* More conversions.
* Configurable class properties to ignore.
