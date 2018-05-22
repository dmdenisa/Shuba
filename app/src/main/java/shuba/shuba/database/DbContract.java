package shuba.shuba.database;

import android.provider.BaseColumns;

public interface DbContract {

    interface User extends BaseColumns {
        /**
         * The SQLite table name for this entity
         */
        String TABLE                = "user";

        /**
         * The SQLite table columns
         */
        String NAME                 = "name";
        String USERNAME             = "username";
        String PASSWORD             = "password";
        String EMAIL                = "email";
        String GROUP_NAME           = "group_name";
        String LEVEL                = "level";
        String PICTURE              = "picture";
        String LOGIN                = "login";

    }

    interface Group extends BaseColumns {
        /**
         * The SQLite table name for this entity
         */
        String TABLE            = "groups";

        /**
         * The SQLite table columns
         */
        String NAME             = "name";
        String DESCRIPTION      = "description";
        String MANAGER          = "manager_id";
        String MAXLEVEL         = "maxlevel";
        String NOMEMBERS        = "nomembers";
    }

    interface Task extends BaseColumns {
        /**
         * The SQLite table name for this entity
         */
        String TABLE            = "tasks";

        /**
         * The SQLite table columns
         */
        String NAME             = "name";
        String DESCRIPTION      = "description";
        String STATE            = "state";
        String NUMBER           = "number";
        String GROUP            = "group";
    }
}