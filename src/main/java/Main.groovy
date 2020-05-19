import groovy.yaml.YamlSlurper
import org.apache.groovy.json.internal.LazyMap

class Main{
    static void main(args){
        def yamlFile = new File("test.yml")
        yamlFile.withReader { reader ->
            LazyMap yaml = new YamlSlurper().parse(reader) as LazyMap
            DefinitionsGenerator definitionsGenerator = new DefinitionsGenerator(yaml, DbConfig.sql)
            definitionsGenerator.generate()
            PathsGenerator pathsGenerator = new PathsGenerator(yaml, DbConfig.sql)
            pathsGenerator.generate()
        }
    }
}