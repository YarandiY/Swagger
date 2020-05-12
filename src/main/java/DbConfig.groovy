import groovy.sql.Sql

class DbConfig {
    private static def dbUrl      = "jdbc:postgresql://localhost/groovy"
    private static def dbUser     = "admin"
    private static def dbPassword = "123456"
    private static def dbDriver   =  "org.postgresql.Driver"
    static def sql = Sql.newInstance(dbUrl, dbUser, dbPassword, dbDriver)
}
