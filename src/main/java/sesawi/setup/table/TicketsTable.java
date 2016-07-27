/*
 * Copyright 2015 Samuel Franklyn <sfranklyn@gmail.com>.
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
public class TicketsTable {

    private static final String ticketsCreateSql
            = "create table tickets ("
            + "ticket_id char(32) not null,"
            + "ticket_no char(13),"
            + "owner_name varchar(30),"
            + "location_name varchar(30),"
            + "computer_name varchar(50),"
            + "user_name varchar(50),"
            + "ticket_entry_time datetime,"
            + "ticket_exit_time datetime,"
            + "ticket_police_no varchar(15),"
            + "price_code char(5),"
            + "price_entry decimal(5,0) default 0,"
            + "price_entry_hour decimal(1,0) default 0,"
            + "price_per_hour decimal(5,0) default 0,"
            + "price_lost decimal(5,0) default 0,"
            + "ticket_duration decimal(3,0) default 0,"
            + "ticket_lost bit(1) default b'0',"
            + "ticket_price decimal(9,0) default 0,"
            + "constraint primary key (ticket_id)"
            + ");";

    public void drop(Statement stmt) throws SQLException {
        stmt.executeUpdate("drop table if exists tickets;");
    }

    public void create(Statement stmt) throws SQLException {
        stmt.executeUpdate(ticketsCreateSql);
    }

}
