package android.sio2.efficom.fr.applitoto.model;

import java.util.Date;
import java.util.List;

/**
 * Created by phuveteau on 13/02/2018.
 */


public class Intervention {
    public String success;
    public String message;

    public List<Liste_int> liste_int;

    public static class Liste_int {

        public String id_inter;
        public Date date_inter;
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
        public int pending;
        public String duration;


    }
}

