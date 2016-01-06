package ca.redtoad.loadprop

import spock.lang.Specification

class PropertiesLoaderSpec extends Specification {

    def loader = new PropertiesLoader()

    def props = '''\
intField = 100
byteField = 0x7F
shortField = 100
longField = 100
floatField = 3.14
doubleField = 2.71
booleanField = false
charField = a
'''
    def 'Primitive fields should load.'() {
        when:
        def loaded = loader.load(PrimitiveFields, props)

        then:
        loaded.intField == 100
        loaded.byteField == 0x7F
        loaded.shortField == 100
        loaded.longField == 100
        loaded.floatField == 3.14f
        loaded.doubleField == 2.71d
        loaded.booleanField == false
        loaded.charField == 'a'
    }

    def 'Object fields should load.'() {
        when:
        def loaded = loader.load(ObjectFields, props)

        then:
        loaded.intField == 100
        loaded.byteField == 0x7F
        loaded.shortField == 100
        loaded.longField == 100
        loaded.floatField == 3.14f
        loaded.doubleField == 2.71d
        loaded.booleanField == false
        loaded.charField == 'a'
    }

    def 'Bean properties should load.'() {
        when:
        def loaded = loader.load(BeanProperties, props)

        then:
        loaded.intField == 100
        loaded.byteField == 0x7F
        loaded.shortField == 100
        loaded.longField == 100
        loaded.floatField == 3.14f
        loaded.doubleField == 2.71d
        loaded.booleanField == false
        loaded.charField == 'a'
    }

    def 'Class missing property that exists in the file should cause error.'() {
        when:
        def loaded = loader.load(BeanProperties, props + "\nextra=foo")

        then:
        def e = thrown(PropertiesLoaderException)
        e.message == "Properties in properties file not bound to properties class: extra."
    }

    def 'Class with property that is missing from the file should cause error.'() {
        when:
        def loaded = loader.load(BeanProperties, props.readLines().drop(1).join('\n'))

        then:
        def e = thrown(PropertiesLoaderException)
        e.message == "Properties missing from properties file: intField."
    }

    def 'Class with Optional should load file with optional property.'() {
        when:
        def loaded = loader.load(OptionalProperty, "optional = loaded")

        then:
        loaded.optional == Optional.of('loaded')
    }

    def 'Class with Optional should load file without optional property.'() {
        when:
        def loaded = loader.load(OptionalProperty, "")

        then:
        loaded.optional == Optional.empty()
    }

    def 'Numeric property that can\'t be parsed should cause error.'() {
        when:
        def loaded = loader.load(BeanProperties, props.replaceFirst("3.14", "pi"))

        then:
        def e = thrown(PropertiesLoaderException)
        e.message == "Error setting property floatField to value \"pi\": java.lang.NumberFormatException: For input string: \"pi\""
    }

    def 'Char property longer than 1 character should cause error.'() {
        when:
        def loaded = loader.load(BeanProperties, props.replaceFirst("charField = a", "charField = aa"))

        then:
        def e = thrown(PropertiesLoaderException)
        e.message == "Error setting property charField to value \"aa\": java.lang.IllegalArgumentException: Input \"aa\" is not of length 1"
    }

    def 'Leading zeros should not be interpreted as octal.'() {
        when:
        def loaded = loader.load(BeanProperties, props.replaceFirst("intField = 100", "intField = 0100"))

        then:
        loaded.intField == 100
    }

}