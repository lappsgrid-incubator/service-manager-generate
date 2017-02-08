package org.anc.lapps.setup.smg

class MapDelegate
{
   def map
   
   public MapDelegate(Map map)
   {
      this.map = map
   }
   
   def propertyMissing(String name, value)
   {
      map[name] = value
   }
}
