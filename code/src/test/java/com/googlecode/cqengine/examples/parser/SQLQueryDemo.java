/**
 * Copyright 2012-2015 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.cqengine.examples.parser;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.createAttributes;

import java.util.Date;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.codegen.AttributeBytecodeGenerator;
import com.googlecode.cqengine.query.parser.cqn.support.DateMathParser;
import com.googlecode.cqengine.query.parser.sql.SQLParser;
import com.googlecode.cqengine.resultset.ResultSet;
import com.googlecode.cqengine.testutil.Car;
import com.googlecode.cqengine.testutil.CarFactory;

/**
 * Demonstrates creating a collection and running an SQL query on it.
 * <p/>
 * Attribute are generated automatically using {@link AttributeBytecodeGenerator}.
 *
 * @author niall.gallagher
 */
public class SQLQueryDemo {

    public static void main(String[] args) {
        SQLParser<Car> parser = SQLParser.forPojoWithAttributes(Car.class, createAttributes(Car.class));
        IndexedCollection<Car> cars = new ConcurrentIndexedCollection<Car>();
        cars.addAll(CarFactory.createCollectionOfCars(10));

        Date fechaActual = new Date();
        parser.registerValueParser(Date.class, new DateMathParser());
        
        // The syntax around the command with the ' character works because of the modification on DateMathParser
        // line 81. IF not modified, the example with the Date attribute and the command don't work with any tested syntax by me.
        ResultSet<Car> results = parser.retrieve(cars, "SELECT * FROM orders WHERE (date < '+0DAY')");
        
        ResultSet<Car> results2 = parser.retrieve(cars, "SELECT * FROM cars WHERE (" +
                                        "(manufacturer = 'Ford' OR manufacturer = 'Honda') " +
                                        "AND price <= 5000.0 " +
                                        "AND color NOT IN ('GREEN', 'WHITE')" +
                                        "AND date < '+2YEARS') " +
                                        "ORDER BY manufacturer DESC, price ASC");

        for (Car car : results) {
            System.out.println(car); // Prints: Honda Accord, Ford Fusion, Ford Focus
        }
        
        for (Car car : results2) {
            System.out.println(car); // Prints: Honda Accord, Ford Fusion, Ford Focus
        }
    }
}
