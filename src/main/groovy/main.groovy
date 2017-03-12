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

PdxInstance instance = cache.createPdxInstanceFactory("Bar")
  .writeString("dog", "bark")
  .writeObject("foo", instanceOfFoo)
  .create()

def o = new DataBag(pdx: instanceOfFoo)

println o.bar // prints 1
println o.baz // prints 2
println o.qux // prints 3
println o.nah // prints null

def x = new DataBag(pdx: instance)
println x.dog
println x.foo.bar // prints 1
