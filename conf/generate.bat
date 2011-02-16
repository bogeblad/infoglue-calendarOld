rem java -cp .;..\lib\commons-logging-1.0.4.jar;..\lib\log4j-1.2.8.jar;..\lib\hibernate-tools.jar;..\lib\hibernate2.jar; net.sf.hibernate.tool.class2hbm.MapGenerator --output=Calendar.hbm.xml org.infoglue.calendar.entities.Calendar 

java -cp .;..\build;..\lib\commons-logging-1.0.4.jar;..\lib\log4j-1.2.8.jar;..\lib\hibernate-tools.jar;..\lib\hibernate2.jar; net.sf.hibernate.tool.class2hbm.MapGenerator --output=Event.hbm.xml org.infoglue.calendar.entities.Event

rem java -cp .;commons-logging-1.0.4.jar;log4j-1.2.8.jar;hibernate-tools.jar;hibernate2.jar; net.sf.hibernate.tool.class2hbm.MapGenerator --output=Location.hbm.xml org.infoglue.calendar.entities.Location

rem java -cp .;commons-logging-1.0.4.jar;log4j-1.2.8.jar;hibernate-tools.jar;hibernate2.jar; net.sf.hibernate.tool.class2hbm.MapGenerator --output=Category.hbm.xml org.infoglue.calendar.entities.Category

rem java -cp .;commons-logging-1.0.4.jar;log4j-1.2.8.jar;hibernate-tools.jar;hibernate2.jar; net.sf.hibernate.tool.class2hbm.MapGenerator --output=Participant.hbm.xml org.infoglue.calendar.entities.Participant

rem java -cp .;commons-logging-1.0.4.jar;log4j-1.2.8.jar;hibernate-tools.jar;hibernate2.jar; net.sf.hibernate.tool.class2hbm.MapGenerator --output=Resource.hbm.xml org.infoglue.calendar.entities.Resource