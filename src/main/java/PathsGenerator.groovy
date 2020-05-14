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
        sql.execute("DROP TABLE IF EXISTS PATH;")
    }

    private void createTables(){
        sql.execute "create table PATH (path_id serial NOT NULL," +
                " url VARCHAR (200), tag VARCHAR (200)," +
                " method VARCHAR (200), summary VARCHAR (200)," +
                " operationId VARCHAR (200), consumes VARCHAR (200)," +
                " produces VARCHAR (200), parameters VARCHAR (1200)," +
                " responses VARCHAR (200), security VARCHAR (200))"
    }

    private void insertPath(path, String method){
        for(item in path.getValue()){
            sql.executeInsert (" INSERT INTO PATH ( url, tag ,method, summary, operationId, consumes, produces, parameters, responses, security)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?) " , [path.key, item.getValue().tags.toString(),
                                                        method, item.getValue().summary,
                                                        item.getValue().operationId , item.getValue().consumes.toString(),
                                                        item.getValue().produces.toString(), item.getValue().parameters.toString(),
                                                        "item.getValue().responses.toString()", "item.getValue().security.toString()"])
        }
    }

    void generate() {
        removeTables()
        createTables()
        for (path in paths){
            println(path.key + " : ")
            if(path.getValue().get != null)
                insertPath(path,"get")
            if(path.getValue().post != null)
                insertPath(path,"post")
            if(path.getValue().delete != null)
                insertPath(path,"delete")
            if(path.getValue().put != null)
                insertPath(path,"put")
        }
    }
}
