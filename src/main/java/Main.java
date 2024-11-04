import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создание файла CSV
        String[] employee = "1,John,Smith,USA,25".split(",");
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv", true))) {
            writer.writeNext(employee);
            employee = "2,Ivan,Petrov,RU,23".split(",");
            writer.writeNext(employee);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);

        // Создание файла XML
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = builder.newDocument();

        Element staff = document.createElement("staff");
        document.appendChild(staff);
        Element employee = document.createElement("employee");
        staff.appendChild(employee);
        Element id = document.createElement("id");
        id.appendChild(document.createTextNode("1"));
        employee.appendChild(id);
        Element firstName = document.createElement("firstName");
        id.appendChild(document.createTextNode("John"));
        employee.appendChild(firstName);
        Element lastName = document.createElement("lastName");
        id.appendChild(document.createTextNode("Smith"));
        employee.appendChild(lastName);
        Element country = document.createElement("country");
        id.appendChild(document.createTextNode("USA"));
        employee.appendChild(country);
        Element age = document.createElement("age");
        id.appendChild(document.createTextNode("25"));
        employee.appendChild(age);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);

        List<Employee> listXML = parseXML("data.xml");
        String jsonXML = listToJson(listXML);

    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<T>>() {}.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader("fileName"))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            staff.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private static List<Employee> parseXML(String s) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File("data.xml"));
        Node staff = doc.getDocumentElement();
        read(staff);
        //NodeList nodeList = staff.getChildNodes();

        return List.of();
    }

    private static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                //System. out.println("Текущий узел: " + node_.getNodeName()); НЕ АКТУАЛЬНО
                // Создать елемент employee
                Employee employee = new Employee();
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();
                for (int a = 0; a < map.getLength(); a++) {
                    String attrName = map.item(a).getNodeName();
                    String attrValue = map.item(a).getNodeValue();
                    //System. out.println("Атрибут: " + attrName + "; значение: " + attrValue); НЕ АКУТАЛЬНО
                    //Добавить в элемент значения
                }
                read(node_);
            }
        }
    }
}