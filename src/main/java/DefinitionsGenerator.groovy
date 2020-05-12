import groovy.sql.Sql
import  org.apache.groovy.json.internal.LazyMap

class DefinitionsGenerator {
    private  LazyMap yamlFile;
    private  LazyMap models;
    private  Sql sql;

    DefinitionsGenerator(LazyMap yamlFile, Sql sql){
        this.yamlFile = yamlFile
        this.sql = sql
        models = yamlFile.definitions
    }

    void generate(){
        sql.execute("DROP TABLE IF EXISTS PROPERTY;")
        sql.execute("DROP TABLE IF EXISTS MODEL;")
        sql.execute "create table MODEL (model_id serial NOT NULL," +
                " name VARCHAR (100), type VARCHAR (100)," +
                " required VARCHAR (100), xml VARCHAR (100)," +
                "CONSTRAINT PK_model PRIMARY KEY ( model_id))"
        sql.execute "create table PROPERTY (property_id serial NOT NULL," +
                " name VARCHAR (100), type VARCHAR (100)," +
                "CONSTRAINT PK_property PRIMARY KEY ( property_id)," +
                " CONSTRAINT FK_21 FOREIGN KEY ( model_id) REFERENCES model ( model_id), model_id integer NOT NULL, " +
                " description VARCHAR (200), enum VARCHAR (100)," +
                " items VARCHAR (100)," +
                " ref VARCHAR (100)," +
                " format VARCHAR (100), xml VARCHAR (100))"


        for (model in models) {
            println(model.key + " :")
            sql.executeInsert (" INSERT INTO MODEL ( name, type, required, xml)" +
                    " VALUES (?,?,?,?) " , [model.key, model.getValue().type,
                                            model.getValue().required.toString(),
                                            model.getValue().xml.toString()]);
            def model_id = sql.rows("select model_id from model where name=\'" + model.key + "\'").get(0).get("model_id")
            println("\t properties : " )
            for (property in model.getValue().properties){
                println("\t \t " + property)
                sql.executeInsert (" INSERT INTO PROPERTY ( name, type, model_id, description," +
                        " enum, items, ref, format, xml) VALUES (?, ?, ? ,? , ?, ?, ?, ?, ?) " ,
                        [property.key.toString(), property.getValue().type, model_id,
                         property.getValue().description, property.getValue().enum.toString(),
                         property.getValue().items.toString(), property.getValue().$ref.toString(),
                         property.getValue().format.toString(), property.getValue().xml.toString()]);
            }
        }
    }
}
