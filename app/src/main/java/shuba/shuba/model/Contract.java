package shuba.shuba.model;

public interface Contract {

    interface Dialog {
        String POSITIVE_BUTTON = "positive_button";
        String NEGATIVE_BUTTON = "negative_button";
        String MESSAGE = "message";
        String TITLE = "title";
    }

    interface Preferences {
        String PREFERENCE_SCREEN_TYPE = "preference_screen_type";
        String AUTH_HASH = "auth_hash";
        String USERNAME = ProfileActivity.USERNAME;

        interface Groups {
            String AFFILIATION = "affiliation";
            String SORT = "sort";
        }
    }

    interface ProfileActivity {
        String USERNAME = "username";
        String GROUPNAME = "groupname";
    }


}
