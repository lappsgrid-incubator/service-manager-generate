package org.anc.lapps.setup.smg

class TestDriver {

   public TestDriver()
   {
   }

   static main(args) 
   {
      ServiceManagerGenerator.main(['src/test/resources/ServiceManager.config'])
   }

}
