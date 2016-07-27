/*
 * Copyright 2016 Samuel Franklyn <sfranklyn@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sesawi.setup.table;

import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Samuel Franklyn <sfranklyn@gmail.com>
 */
public class ShiftsTable {

    private static final String SHIFTS_CREATE_SQL
            = "create table shifts ("
            + "shift_id int not null auto_increment,"
            + "user_id int not null,"
            + "computer_id int not null,"
            + "shift_start datetime,"
            + "shift_end datetime,"
            + "constraint fk_shifts1 foreign key (user_id)"
            + "  references users (user_id),"
            + "constraint fk_shifts2 foreign key (computer_id)"
            + "  references computers (computer_id),"
            + "constraint primary key (shift_id),"
            + "constraint nk_shifts unique (shift_id,user_id,computer_id)"
            + ");";

    public void drop(Statement stmt) throws SQLException {
        stmt.executeUpdate("drop table if exists shifts;");
    }

    public void create(Statement stmt) throws SQLException {
        stmt.executeUpdate(SHIFTS_CREATE_SQL);
    }

}
