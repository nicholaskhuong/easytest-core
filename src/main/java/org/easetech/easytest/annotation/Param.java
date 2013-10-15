
package org.easetech.easytest.annotation;

import org.easetech.easytest.converter.ConversionDelegator;

import org.easetech.easytest.converter.MapConverter;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.easetech.easytest.converter.AbstractConverter;
import org.easetech.easytest.converter.Converter;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.converter.ParamAwareConverter;
import org.easetech.easytest.internal.EasyParamSignature;
import org.easetech.easytest.util.DataContext;
import org.easetech.easytest.util.GeneralUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parameter level optional annotation that converts the data for EasyTest based test methods to consume. This
 * Annotation gives the ability to pass input parameters to the test method. EasyTest will automatically call the test
 * method as many times as there are number of data sets to be run against that particular method. For example, if the
 * user has specified 5 data set for a single test method, EasyTest will call the method five times, each time providing
 * the test data that was provided by the user. <br>
 * The annotation is used in conjunction with {@link DataLoader} annotation. {@link DataLoader} annotation is used to
 * provide test data to the test cases. </br> <br>
 * The annotation is optional. In case the name of the input parameter provided in the data file(XML, Excel, CSV or
 * custom) is same as the name of the input parameter type then this annotation can be omitted. For eg:<br>
 * <code><B>
 * public void testWithStrongParameters(LibraryId id ,
 * (@)Param(name="itemid") ItemId
 * itemId) { .... } </B>
 * </code> <br>
 * In the above example we have not provided @Param annotation to the input parameter LibraryId. In this case the test
 * parameter name in the test file should be LibraryId. You have to take care that in scenario where the input
 * parameters are of the same type, the names should be different. Thus if you have two input parameters of type
 * LibraryId then you should provide atleast @Param annotation on one of the input parameters.
 * 
 * The annotation contains a single mandatory field :
 * 
 * <li><B> name</B> : the name of the parameter(Mandatory) as is present in the input test data file. <li>In case the
 * param annotation is not specified and the Parameter type is Map, {@link DataSupplier} simply provides the HashMap
 * instance that was created while loading the data. This {@link HashMap} represents a single set of test data for the
 * test method.</li> <li>In case the param name is specified along with the {link @Param} annotation, the framework will
 * look for the parameter with the specified name in the loaded test data. <br>
 * 
 * Moreover, the framework supports PropertyEditors support for strongly typed objects. If you have a custom object and
 * its property editor in the same package, the EasyTest framework will convert the String value to your specified
 * custom object by calling the right property editor and pass an instance of custom object to your test case. This
 * provides the users facility to write test cases such as this : <br>
 * <code>
 * 
 * @Test
 * @DataLoader(filePaths ={ "getItemsData.csv" }) <br>public void testWithStrongParameters(LibraryId id
 *                       ,@Param(name="itemid") ItemId itemId) { .... } </code> <br>
 * <br>
 *                       <li>Example of using Map to get the entire data:</li></br> <br>
 *                       <code><br>
 * @Test (@)DataLoader(filePaths= {"getItemsData.csv" })<br> public void testGetItemsWithoutFileType( Map<String,
 *       Object> inputData) {<br> ........
 * 
 *       }</code>
 * 
 * 
 * <br>
 * <br>
 *       The EasyTest framework also supports custom {@link Converter}s. Converters are a mechanism for the user to
 *       translate a map of key/value pair into custom objects that can be passed as input parameters to the test cases.
 *       For example, if you want to pass a complex object as input parameter to a test case then you will simply write
 *       a custom converter that extends {@link AbstractConverter} and will override the
 *       {@link AbstractConverter#convert(Map)} method. You can look at example of CustomConverter here :
 *       https://github.
 *       com/EaseTech/easytest-core/blob/master/src/test/java/org/easetech/easytest/example/ItemConverter.java
 * 
 * <br>
 *       If you want to pass a Collection type, then EasyTest framework provides the functionality to instantiate the
 *       Collection class for you and pass in the right generic parameter if possible. For eg. if you have a test method
 *       like this :<br>
 * <br>
 *       <code>
 * 
 *  (At)Test<br>
 *   public void testArrayList(@Param(name="items") ArrayList&lt;ItemId> items){<br>
 *       Assert.assertNotNull(items);<br>
 *      for(ItemId item : items){<br>
 *          System.out.println("testArrayList : "+item);<br>
 *      &nbsp;&nbsp;}<br>
 *  }<br>
 * 
 * then all you have to do is :
 * <li> pass the list of itemIds as <B>":"</B> separated list in the test data file(XML, CSV,Excel or custom), for eg: 23:56:908:666</li><br>
 * <li> and register an editor or converter for converting the String data to object.<br>
 * In case the generic type argument to the Collection is a standard Java type(Date, Character, Timestamp, Long, Interger, Float, Double etc) 
 * then you don't have to do anything and the framework will take care of converting the String data to the requested type. 
 * <br><br>
 * Finally, even though the EasyTest framework is compiled with JDK 1.5, it does not stop you from using Java 6 Collection Types in case you are running your code on JRE 6.
 * For using Java 6 Collection type(like Deque , LinkedBlockingDeque etc) in your test cases, all you have to do is register an empty implementation of {@link AbstractConverter} in the {@link BeforeClass} method.
 * Note that you should pass only the Concrete type as parameter argument while extending the {@link AbstractConverter} and not the Abstract type or the interface type.
 * You can always override the default implementation of creating a specific type instance for use in your test cases by overriding the {@link AbstractConverter#instanceOfType()} method.
 * 
 * 
 * @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Param {

    /** The name of the parameter for which value needs to be fetched from the data set */
    String name();

    /**
     * Static class that returns the data as a list of {@link PotentialAssignment}. 
     * This is the place where we can specify what the data type
     * of the returned data would be. We can also specify different return types for different test methods.
     * 
     */
    static class DataSupplier {

       

        /**
         * Method to return the list of data for the given Test method
         * 
         * @param signature the {@link ParameterSignature}
         * @param testMethodName the name of the currently executing test method
         * @return the list of {@link PotentialAssignment}
         */

        public List<PotentialAssignment> getValueSources(String testMethodName, EasyParamSignature signature) {
            Param provider = signature.getAnnotation(Param.class);
            if (testMethodName == null) {
                Assert
                    .fail("The framework could not locate the test data for the test method. If you are using TestData annotation, " +
                    		"make sure you specify the test method name in the data file. "
                        + "In case you are using ParametersSuppliedBy annotation, make sure you are using the right ParameterSupplier subclass.");
            }
            List<PotentialAssignment> listOfData = null;
            Map<String, List<Map<String, Object>>> data = DataContext.getConvertedData();
            List<Map<String, Object>> methodData = data.get(testMethodName);
            if (methodData == null) {
                Assert.fail("Data does not exist for the specified method with name :" + testMethodName
                        + " .Please check that the Data file contains the data for the given method name. A possible cause could be spelling mismatch.");
            }
            List<Map<String , Object>> testData = data.get(testMethodName);
            listOfData = new ConversionDelegator(signature , provider != null ? provider.name() : null).convert(testData);

            return listOfData;
        }



    }
}
