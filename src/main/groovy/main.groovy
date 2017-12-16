import org.apache.geode.cache.*
import org.apache.geode.pdx.*

class Foo {
  String bar, baz, qux
}

class Bar {
  String dog
  Foo foo
}

class DataBag implements GroovyInterceptable {

  PdxInstance pdx

  void setProperty(String field, value) {
    println "Set ${field} to ${value}"
  }

  def getProperty(String field) {
    def o = pdx.getField(field)
    (o in PdxInstance) ? new DataBag(pdx: o) : o
  }
}

println "Hello world!"

def cache = new CacheFactory().create()
cache.setReadSerialized(true)

PdxInstance instanceOfFoo = cache.createPdxInstanceFactory("Foo")
  .writeString("bar", "1")
  .writeString("baz", "2")
  .writeString("qux", "3")
  .create()

PdxInstance instanceOfBar = cache.createPdxInstanceFactory("Bar")
  .writeString("dog", "bark")
  .writeObject("foo", instanceOfFoo)
  .create()

def Foo = new DataBag(pdx: instanceOfFoo)

println Foo.bar // prints 1
println Foo.baz // prints 2
println Foo.qux // prints 3
println Foo.nah // prints null

def Bar = new DataBag(pdx: instanceOfBar)
println Bar.dog
println Bar.foo.bar // prints 1

