import groovy.yaml.YamlSlurper
import groovy.json.JsonOutput;


class Main{
    static void main(args){
        yamlFile.withReader { reader ->
            def yaml = new YamlSlurper().parse(reader)
//            def json = JsonOutput.toJson(yaml)
//            println JsonOutput.prettyPrint(json)
//            println("*************************************************")
            def models = yaml.definitions
            for (model in models) {
                println(model.getKey() + " : ")
                println("\t type : "  + model.getValue().type)
                println("\t required : "  + model.getValue().required)
                println("\t properties : " )
                for (propertie in model.getValue().properties){
                    println("\t \t " + propertie)
                }
                println("\t xml : "  + model.getValue().xml)
            }


        }
    }
    static def yamlFile = new File("src/test.yml")

}