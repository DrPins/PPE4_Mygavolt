package android.sio2.efficom.fr.applitoto.model;

import java.util.List;

/**
 * Created by yassine on 13/02/2018.
 */


public class Intervention {
    public String success;
    public String employeeFound;
    public String message;
    public String action;
    public idInterventionTotal id_intervention_total;

    public static class idInterventionTotal {

        public List<Liste_int> liste_int;

        public static class Liste_int{

            public String id_inter;
            public String date_inter;
            public String firstname;
            public String lastname;
            public String company;
            public String address1;
            public String address2;
            public String zipcode;
            public String city;
            public String phone;
            public String motive;
            public String report;
            public String pending;
            public String duration;





        }
    }

}



/*
public class Forecast2 {
    public String cod;
    public float message;

    public List<Item> list;

    public static class Item{
        public long dt;

        public Wind wind;

        public Main main;

        public static class Wind{
            public float speed;
            public float deg;
        }

        @Override
        public String toString() {
            return dt + ", deg: " + wind.deg;
        }

        public static class Main {
            public float temp;
        }
    }
}
*/