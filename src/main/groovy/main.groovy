import org.apache.geode.cache.*
import org.apache.geode.pdx.*

class Foo {
  String bar, baz, qux
}

class DataBag implements GroovyInterceptable {

  PdxInstance pdx

  void setProperty(String field, value) {
    println "Set ${field} to ${value}"
  }

  def getProperty(String field) {
    pdx.getField(field)
  }
}

println "Hello world!"

def cache = new CacheFactory().create()

PdxInstance instance = cache.createPdxInstanceFactory("Foo")
  .writeString("bar", "1")
  .writeString("baz", "2")
  .writeString("qux", "3")
  .create()

def o = new DataBag(pdx: instance)

println o.bar // prints 1
println o.baz // prints 2
println o.qux // prints 3
