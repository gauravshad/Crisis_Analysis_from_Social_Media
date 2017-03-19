/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Gaurav
 */

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Permission;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.FusiontablesScopes;
import com.google.api.services.fusiontables.model.Column;
import com.google.api.services.fusiontables.model.Table;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyFusionTable {
        private static String tid;
    private static final String MY_APP_NAME = "Crisis Analysis";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static HttpTransport sHttpTransport;

    private Fusiontables mFusionTable;
    private Drive mDrive;

    static {
        try {
            sHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException ex) {
            Logger.getLogger(MyFusionTable.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MyFusionTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        // Sample usage
        MyFusionTable myFusionTable = new MyFusionTable();
        myFusionTable.createNewTable("crisis-data", "/home/ubuntu/ccproject/filteredout.txt");
    }

    public MyFusionTable() throws GeneralSecurityException, IOException {
        GoogleCredential credential = new GoogleCredential.Builder()
              .setTransport(sHttpTransport)
              .setJsonFactory(JSON_FACTORY)
              .setServiceAccountId("crisis-analysis@divine-quest-98112.iam.gserviceaccount.com") // TODO: Add proper email.
              .setServiceAccountScopes(Arrays.asList(FusiontablesScopes.FUSIONTABLES, DriveScopes.DRIVE))
              .setServiceAccountPrivateKeyFromP12File(new File("/home/ubuntu/ccproject/My Project-3b882e290dfc.p12")) // TODO: Add proper key
              .build();
        mFusionTable = new Fusiontables.Builder(sHttpTransport, JSON_FACTORY, credential)
                .setApplicationName(MY_APP_NAME).build();
        mDrive = new Drive.Builder(sHttpTransport, JSON_FACTORY, credential)
                .setApplicationName(MY_APP_NAME).build();
    }

    public String createNewTable(String tableName, String csvFilePath) throws IOException {
        // Must have isExportable set to true when using service account.
        Table table = new Table().setName(tableName).setIsExportable(true);
        List<Column> columns = new ArrayList<>();
        // TODO: Update with real column names and types.
        columns.add(new Column().setName("location").setType("LOCATION"));
        columns.add(new Column().setName("Crisis").setType("STRING"));
        columns.add(new Column().setName("Count").setType("NUMBER"));
        table.setColumns(columns);
        String tableId = mFusionTable.table().insert(table).execute().getTableId();
        
        mDrive.permissions().insert(tableId, getPermission1()).execute();
        mDrive.permissions().insert(tableId, getPermission2()).execute();

        mFusionTable.table().replaceRows(tableId).setDelimiter("~");
        mFusionTable.table().importRows(tableId,
                new FileContent("application/octet-stream", new File(csvFilePath))).execute();
        tid = tableId;
         
        return tableId;
    }

    public String getTableid(){
        
        return tid;
    }
    // Just for debugging purposes.
    public String debugGetListOfTables() throws IOException {
        return mFusionTable.table().list().execute().getItems().toString();
    }

    // Just for debugging purposes.
    public String debugGetTableDetails(String tableId) throws IOException {
        return mFusionTable.table().get(tableId).execute().toPrettyString();
    }

   

    private Permission getPermission1() {
        Permission permission = new Permission();
        // TODO: Add proper owner email address.
        permission.setValue("gauravshad@gmail.com");
        permission.setType("user");
        permission.setRole("owner");
        
        
        return permission;
    }
    
      private Permission getPermission2() {
        Permission permission = new Permission();
        // TODO: Add proper owner email address.
        permission.setValue("");
        permission.setType("anyone");
        permission.setRole("writer");
        
        
        return permission;
    }
}
