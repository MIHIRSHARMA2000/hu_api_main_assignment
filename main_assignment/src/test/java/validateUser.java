import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;
@Listeners (ExtentReport.class)
public class validateUser extends ExcelRead{

    /*private String username;
    private String email;
    private String password;
    public String tokenGenerated;

    @BeforeMethod
    public void registerUser() throws IOException {
        File file = new File("C:\\Users\\mihirsharma\\Downloads\\testUserData.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet("Sheet1");
        XSSFRow row1 = sheet.getRow(1);
        XSSFCell cell1 = row1.getCell(0);
        XSSFCell cell2 = row1.getCell(1);
        XSSFCell cell3 = row1.getCell(2);
        XSSFCell cell4 = row1.getCell(3);
        username = cell1.getStringCellValue();
        System.out.println(username);
        email = cell2.getStringCellValue();
        System.out.println(email);
        password = cell3.getStringCellValue();
        System.out.println(password);
    }*/
    @BeforeMethod
    public  void readFromExcel() throws IOException {
        readExcel();

    }
    @Test(priority = 1)
    public void registrationAndLogin() throws IOException {
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com";
        RequestSpecification request = RestAssured.given();
        String payload = "{\n" +
                "  \"name\" : \""+username+"\",\n" +
                "  \"email\" : \""+email+"\",\n" +
                "  \"password\" : \""+password+"\"\n" +
                "}";
        request.header("Content-Type", "application/json");
        Response responsefromGeneratedToken = request.body(payload).post("/user/register");
        responsefromGeneratedToken.prettyPrint();
        String jsonString = responsefromGeneratedToken.getBody().asString();
        tokenGenerated = JsonPath.from(jsonString).get("token");
        request.header("Authorization", "Bearer" + tokenGenerated)
                .header("Content-Type", "application/json");
        String loginDetails = "{\n" +
                "  \"email\" : \""+email+"\",\n" +
                "  \"password\" : \""+password+"\"\n" +
                "}";
        Response responseLogin = request.body(loginDetails).post("/user/login");
        responseLogin.prettyPrint();
    }

    @Test(priority = 2)
    public void addTask() throws IOException, NullPointerException{
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com/task";
        RequestSpecification request = RestAssured.given();
        request.header("Authorization", "Bearer " + tokenGenerated)
                .header("Content-Type", "application/json");
        FileInputStream inputStream = new FileInputStream("C:\\Users\\mihirsharma\\Downloads\\tasks.xlsx");
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet("Sheet1");
        int rows = sheet.getPhysicalNumberOfRows();
        int cols = sheet.getRow(0).getLastCellNum();
        String description = null;
        String task = null;
        for (int i = 1; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j == 0) {
                    description = sheet.getRow(i).getCell(j).getStringCellValue();
                }
                if (j == 1) {
                    task = sheet.getRow(i).getCell(j).getStringCellValue();
                }
            }
            //System.out.println(description+" "+" "+task);
            String addTaskJson = "{\n" +
                    "\t\""+description+"\": \""+task+"\"\n" +
                    "}";
            Response responseaddTask = request.body(addTaskJson).post();

            responseaddTask.prettyPrint();
            String str = responseaddTask.getBody().asString();
            String getDescription = JsonPath.from(str).get("data.description");
            //Assert.assertEquals(task,lnu);

                if (task.contains(getDescription) == true) {
                    System.out.println("Validated");

                } else {
                    System.out.println(" Not Validated");
                }

            //wb.close();
            //inputStream.close();
        }
    }
    @Test(priority = 3)
    public void validateUser(){
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com/user/me";
        RequestSpecification request = RestAssured.given();
        request.header("Authorization","Bearer "+ tokenGenerated)
                .header("Content-Type","application/json");
        Response responsevalidateUser = request.get();
        responsevalidateUser.prettyPrint();
    }
    @Test(priority = 4)
    public void getTask(){
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com/task";
        RequestSpecification request = RestAssured.given();
        request.header("Authorization","Bearer "+ tokenGenerated)
                .header("Content-Type","application/json");
        Response responsegetTask = request.get();
        responsegetTask.prettyPrint();
    }
    @Test(priority = 5)
    public void paginationFor2(){
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com";
        RequestSpecification request2 = RestAssured.given();
        request2.header("Authorization","Bearer "+ tokenGenerated)
                .header("Content-Type","application/json");
        Response response2 = request2.get("/task?limit=2");
        response2.prettyPrint();
    }
    @Test(priority = 6)
    public void paginationFor5(){
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com";
        RequestSpecification request5 = RestAssured.given();
        request5.header("Authorization","Bearer "+ tokenGenerated)
                .header("Content-Type","application/json");
        Response response5 = request5.get("/task?limit=5");
        response5.prettyPrint();
    }
    @Test(priority = 7)
    public void paginationFor10(){
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com";
        RequestSpecification request10 = RestAssured.given();
        request10.header("Authorization","Bearer "+ tokenGenerated)
                .header("Content-Type","application/json");
        Response response10 = request10.get("/task?limit=10");
        response10.prettyPrint();
    }
    @Test (priority = 8)
    public void negativeAuthenticateTest() throws IOException {

        registrationAndLogin();

    }
    @Test (priority = 9)
    public void loginNotRegisterdUser()
    {
        RestAssured.baseURI = "https://api-nodejs-todolist.herokuapp.com";
        RequestSpecification request = RestAssured.given();
        /*request.header("Authorization", "Bearer" + tokenGenerated)
                .header("Content-Type", "application/json");*/
        String loginDetails = "{\n" +
                "  \"email\" : \""+"mihirs340@gmail.com"+"\",\n" +
                "  \"password\" : \""+"1234@123h"+"\"\n" +
                "}";
        Response responseLogin = request.body(loginDetails).post("/user/login");
        responseLogin.prettyPrint();
        String actual = responseLogin.getBody().asString();
        System.out.println(actual);
        String expected ="Unable to login";
        Assert.assertNotEquals(actual,expected);

    }



}

