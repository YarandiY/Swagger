import groovy.sql.Sql
import org.apache.groovy.json.internal.LazyMap

class PathsGenerator{
    private  LazyMap paths
    LazyMap yamlFile
    Sql sql

    PathsGenerator(LazyMap yamlFile, Sql sql){
        this.yamlFile = yamlFile
        this.sql = sql
        paths = yamlFile.paths
    }

    private void removeTables(){
        sql.execute("DROP TABLE IF EXISTS PARAMETER;")
        sql.execute("DROP TABLE IF EXISTS RESPONSE;")
        sql.execute("DROP TABLE IF EXISTS PATH;")
    }

    private void createTables(){
        sql.execute "create table PATH (path_id serial NOT NULL," +
                " url VARCHAR (200), tag VARCHAR (200)," +
                " method VARCHAR (200), summary VARCHAR (200)," +
                " operationId VARCHAR (200), consumes VARCHAR (200)," +
                " produces VARCHAR (200)," +
                " security VARCHAR (200), CONSTRAINT PK_path PRIMARY KEY ( path_id))"

        sql.execute "create table PARAMETER (parameter_id serial NOT NULL," +
                " name VARCHAR (60), in_p VARCHAR (20)," +
                " description VARCHAR (100), required VARCHAR (20) NULL," +
                " type VARCHAR (20) NULL, maximum NUMERIC NULL," +
                " minimum NUMERIC NULL, format VARCHAR (20) NULL," +
                " CONSTRAINT FK_21 FOREIGN KEY ( path_id) REFERENCES path ( path_id), path_id integer NOT NULL, " +
                " schema VARCHAR (60) NULL, CONSTRAINT PK_parameter PRIMARY KEY ( parameter_id))"

        sql.execute "create table RESPONSE (res_id serial NOT NULL," +
                " status_code VARCHAR (10), description VARCHAR (100)," +
                " schema VARCHAR (200), headers VARCHAR (200) NULL," +
                " CONSTRAINT FK_21 FOREIGN KEY ( path_id) REFERENCES path ( path_id), path_id integer NOT NULL, " +
                " CONSTRAINT PK_response PRIMARY KEY ( res_id))"
    }

    private void insertPath(api,path){
            sql.executeInsert (" INSERT INTO PATH ( url, tag ,method, summary, operationId, consumes, produces, security)" +
                    " VALUES (?,?,?,?,?,?,?,?) " , [path.key, api.getValue().tags.toString(),
                                                        api.getKey(), api.getValue().summary,
                                                        api.getValue().operationId , api.getValue().consumes.toString(),
                                                        api.getValue().produces.toString(),
                                                        api.getValue().security.toString()])
    }
    private void insertParameters(parameters, int api_id){
        for(parameter in parameters){
            sql.executeInsert (" INSERT INTO PARAMETER ( name, in_p ,description, required, type, maximum, minimum, format, path_id, schema)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?) " , [parameter.name, parameter.in, parameter.description,
                                                        parameter.required, parameter.type, parameter.maximum,
                                                        parameter.minimum,parameter.format, api_id, parameter.schema.toString()])
        }
    }
    private void insertResponse(responses, int api_id){
        for(res in responses){
            sql.executeInsert (" INSERT INTO RESPONSE ( status_code, description, schema, headers, path_id)" +
                    " VALUES (?,?,?,?,?) " , [res.key, res.getValue().description, res.getValue().schema.toString(), res.getValue().headers.toString(), api_id])
        }
    }

    void generate() {
        removeTables()
        createTables()
        for (path in paths){
            for (api in path.getValue()){
                insertPath(api, path)
                def api_id = sql.rows("select path_id from path where url=\'" + path.key + "\'").get(0).get("path_id")
                insertParameters(api.getValue().parameters, api_id as int)
                insertResponse(api.getValue().responses, api_id as int)
            }
        }
    }
}
