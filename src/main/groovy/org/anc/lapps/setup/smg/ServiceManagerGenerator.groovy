package org.anc.lapps.setup.smg

import groovy.xml.MarkupBuilder

class ServiceManagerGenerator {

   // Map objects for each configuration section.
   def tomcat = [:]
   def database = [:]
   def operator = [:]
   def node = [:]
   def auth = [:]
   def context = [:]

   public ServiceManagerGenerator() {
   }

   void run(File scriptFile) {
      Script script = new GroovyShell().parse(scriptFile)
      ExpandoMetaClass meta = new ExpandoMetaClass(script.class, false)

      meta.Context = { Closure cl ->
         MapDelegate delegate = new MapDelegate(context)
         cl.delegate = delegate
         cl.resolveStrategy = Closure.DELEGATE_FIRST
         cl()
      }

      meta.Tomcat = { Closure cl ->
         MapDelegate delegate = new MapDelegate(tomcat)
         cl.delegate = delegate
         cl.resolveStrategy = Closure.DELEGATE_FIRST
         cl()
      }
      
      meta.Database = { Closure cl ->
         MapDelegate delegate = new MapDelegate(database)
         cl.delegate = delegate
         cl.resolveStrategy = Closure.DELEGATE_FIRST
         cl()
      }

      meta.Node = { Closure cl ->
         MapDelegate delegate = new MapDelegate(node)
         cl.delegate = delegate
         cl.resolveStrategy = Closure.DELEGATE_FIRST
         cl()
      }

      meta.Operator = { Closure cl ->
         MapDelegate delegate = new MapDelegate(operator)
         cl.delegate = delegate
         cl.resolveStrategy = Closure.DELEGATE_FIRST
         cl()
      }

      meta.Auth = { Closure cl ->
         MapDelegate delegate = new MapDelegate(auth)
         cl.delegate = delegate
         cl.resolveStrategy = Closure.DELEGATE_FIRST
         cl()
      }

      meta.initialize()
      script.metaClass = meta
      script.run()

      //def dbName = properties['database']['table']
      File managerXml = new File('service_manager.xml')
      File bpelXml = new File('active-bpel.xml')
      File users = new File('tomcat-users.xml')
      File bpelUsers = new File('tomcat-users-bpel.xml')
      
      writeServiceManagerXml('service_manager.xml')
      writeActiveBpelXml('active-bpel.xml')
      writeTomcatUsers('tomcat-users.xml')
      writeTomcatUsers('tomcat-users-bpel.xml')
      writeAeProperties('langrid.ae.properties')
      writeDbConfig('db.config')
   }

   void writeServiceManagerXml(String filename)
   {
      def writer = new FileWriter(new File(filename))
      def xml = new MarkupBuilder(writer)
      xml.Context(reloadable:'true', displayName:context['displayName']) {
         Resource(name:"jdbc/${database.table}", auth:"Container", type:"javax.sql.DataSource",
               maxActive:100, maxIdle:50, maxWait:10000, username:"${database.username}",
               password:"${database.password}", driverClassName:"org.postgresql.Driver",
               url:"jdbc:postgresql:${database.table}")
         context.each { name,value ->
            if (name != 'displayName')
            {
               Parameter(name:"langrid.${name}", value:"${value}")
            }
         }
         node.each { name, value ->
            Parameter(name:"langrid.node.${name}", value:"${value}")
         }
         operator.each { name, value ->
            Parameter(name:"langrid.operator.${name}", value:"${value}")
         }
         auth.each { name,value ->
            Parameter(name:"appAuth.simpleApp.${name}", value:"${value}")
         }
      }
      writer.close()
      println "Generated ${filename}"
   }
   
   void writeActiveBpelXml(String filename)
   {
      FileWriter writer = new FileWriter(new File(filename))
      MarkupBuilder xml = new MarkupBuilder(writer)
      xml.Context(reloadable:'true', displayName:'ActiveBPEL Process Manager', path:'/active-bpel') {
         Parameter(name:'fromCoreNode.appAuth.authIps', value:auth['authIps'])
         Parameter(name:'fromCoreNode.appAuth.authKey', value:context['activeBpelAppAuthKey'])
      }
      writer.close()
      println "Generated ${filename}"
   }
   
   void writeTomcatUsers(String filename)
   {
      FileWriter writer = new FileWriter(filename)      
      MarkupBuilder xml = new MarkupBuilder(writer)
      xml.'tomcat-users' {
         role(rolename:tomcat.rolename)
         user(username:tomcat.username, password:tomcat.password, roles:"manager-gui,admin-gui,manager-status,${tomcat.rolename}")
      }
      writer.close();
      println "Generated ${filename}"
   }
   
   void writeAeProperties(String filename)
   {
      PrintWriter out = new PrintWriter(filename)
      out.println("langrid.coreNodeUrl=${node.url}")
      out.println("langrid.appAuthKey=${auth.authKey}")
      out.println("langrid.targetNamespaceCacheSize=200")
      out.println("langrid.targetNamespaceCacheTtlSec=600")
      out.println("langrid.serviceIdCacheSize=200")
      out.println("langrid.serviceIdCacheTtlSec=600")
      out.close()
      println "Generated ${filename}"
   }
   
   void writeDbConfig(String filename)
   {
      PrintWriter out = new PrintWriter(filename)
      out.println("DATABASE=${database.name}")
      out.println("ROLENAME=${database.username}")
      out.println("PASSWORD=${database.password}")
      out.close()
      println "Generated ${filename}"
   }
   
   static private void usage()
   {
      println "USAGE"
      println ""
      println "groovy smg.groovy <config_script>"
      println ""
   }

   static void main(args)
   {
      if (args.size() == 0)
      {
         usage()
         return
      }

      if (args[0] == "-v" || args[0] == "-version")
      {
         println ""
         println "ServiceMangagerGeneator v." + org.anc.lapps.setup.smg.version.Version.getVersion()
         println "Copyright 2012 The American National Corpus"
         println ""
         return
      }
      
      File scriptFile = new File(args[0])
      if (!scriptFile.exists())
      {
         println "File not found: ${scriptFile.path}"
         return
      }

      new ServiceManagerGenerator().run(scriptFile)
   }
}
