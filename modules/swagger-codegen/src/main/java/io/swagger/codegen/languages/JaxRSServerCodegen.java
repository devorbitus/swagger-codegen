package io.swagger.codegen.languages;

import io.swagger.codegen.CodegenConfig;
import io.swagger.codegen.CodegenConstants;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenResponse;
import io.swagger.codegen.CodegenType;
import io.swagger.codegen.SupportingFile;
import io.swagger.models.Operation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JaxRSServerCodegen extends JavaClientCodegen implements CodegenConfig {
    protected String invokerPackage = "io.swagger.api";
    protected String groupId = "io.swagger";
    protected String artifactId = "swagger-jaxrs-server";
    protected String artifactVersion = "1.0.0";
    protected String title = "Swagger Server";

    public JaxRSServerCodegen() {
        super.processOpts();

        sourceFolder = System.getProperty("swagger.codegen.jaxrs.sourcefolder", "src/gen/java");

        outputFolder = System.getProperty("swagger.codegen.jaxrs.genfolder", "generated-code/javaJaxRS");
        modelTemplateFiles.put("model.mustache", ".java");
        apiTemplateFiles.put("api.mustache", ".java");
        apiTemplateFiles.put("apiService.mustache", ".java");
        apiTemplateFiles.put("apiServiceImpl.mustache", ".java");
        apiTemplateFiles.put("apiServiceFactory.mustache", ".java");
        templateDir = "JavaJaxRS";
        apiPackage = System.getProperty("swagger.codegen.jaxrs.apipackage", "io.swagger.api");
        domain = System.getProperty("domain", "swagger.io");
        modelPackage = System.getProperty("swagger.codegen.jaxrs.modelpackage", "io.swagger.model");
        exceptionPackage = System.getProperty("swagger.codegen.jaxrs.exceptionpackage", "io.swagger.exception");
        implPackage = System.getProperty("swagger.codegen.jaxrs.impl.source", "io.swagger.impl");
        resourcePackage = System.getProperty("swagger.codegen.jaxrs.resources", "io.swagger.api"); 
        dbPackage = System.getProperty("swagger.codegen.jaxrs.dbpackage", null);
        webXmlPath = System.getProperty("swagger.codegen.jaxrs.webxmlpath", "src/main/webapp/WEB-INF");
        factoryPackage = System.getProperty("swagger.codegen.jaxrs.factory.source", "io.swagger.factory");
        jndi = System.getProperty("swagger.codegen.jaxrs.jndi.datasource", "ChangeThisDatasource.1");
        
        additionalProperties.put(CodegenConstants.INVOKER_PACKAGE, invokerPackage);
        additionalProperties.put(CodegenConstants.GROUP_ID, groupId);
        additionalProperties.put(CodegenConstants.ARTIFACT_ID, artifactId);
        additionalProperties.put(CodegenConstants.ARTIFACT_VERSION, artifactVersion);
        additionalProperties.put("title", title);


        languageSpecificPrimitives = new HashSet<String>(
                Arrays.asList(
                        "String",
                        "Short",
                        "boolean",
                        "Boolean",
                        "Double",
                        "Integer",
                        "Long",
                        "Float")
        );
    }

    public CodegenType getTag() {
        return CodegenType.SERVER;
    }

    public String getName() {
        return "jaxrs";
    }

    public String getHelp() {
        return "Generates a Java JAXRS Server application.";
    }

    @Override
    public void processOpts() {
        super.processOpts();

        supportingFiles.clear();
        supportingFiles.add(new SupportingFile("pom.mustache", "", "pom.xml"));
        supportingFiles.add(new SupportingFile("README.mustache", "", "README.md"));
        supportingFiles.add(new SupportingFile("ApiException.mustache",
                (sourceFolder + File.separator + exceptionPackage).replace(".", java.io.File.separator), "ApiException.java"));
        supportingFiles.add(new SupportingFile("ApiOriginFilter.mustache",
                (sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "ApiOriginFilter.java"));
        supportingFiles.add(new SupportingFile("ApiResponseMessage.mustache",
                (sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "ApiResponseMessage.java"));
        supportingFiles.add(new SupportingFile("Bootstrap.mustache",
        		(sourceFolder + File.separator + apiPackage).replace(".", java.io.File.separator), "Bootstrap.java"));
        supportingFiles.add(new SupportingFile("NotFoundException.mustache",
                (sourceFolder + File.separator + exceptionPackage).replace(".", java.io.File.separator), "NotFoundException.java"));
        supportingFiles.add(new SupportingFile("web.mustache",
                (webXmlPath), "web.xml"));
    }

    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        String basePath = resourcePath;
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1);
        }
        int pos = basePath.indexOf("/");
        if (pos > 0) {
            basePath = basePath.substring(0, pos);
        }

        if (basePath == "") {
            basePath = "default";
        } else {
            if (co.path.startsWith("/" + basePath)) {
                co.path = co.path.substring(("/" + basePath).length());
            }
            co.subresourceOperation = !co.path.isEmpty();
        }
        List<CodegenOperation> opList = operations.get(basePath);
        if (opList == null) {
            opList = new ArrayList<CodegenOperation>();
            operations.put(basePath, opList);
        }
        opList.add(co);
        co.baseName = basePath;
    }

    public Map<String, Object> postProcessOperations(Map<String, Object> objs) {
        Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
        if (operations != null) {
            List<CodegenOperation> ops = (List<CodegenOperation>) operations.get("operation");
            for (CodegenOperation operation : ops) {
                List<CodegenResponse> responses = operation.responses;
                if (responses != null) {
                    for (CodegenResponse resp : responses) {
                        if ("0".equals(resp.code)) {
                            resp.code = "200";
                        }
                    }
                }
                if (operation.returnType == null) {
                    operation.returnType = "Void";
                } else if (operation.returnType.startsWith("List")) {
                    String rt = operation.returnType;
                    int end = rt.lastIndexOf(">");
                    if (end > 0) {
                        operation.returnType = rt.substring("List<".length(), end).trim();
                        operation.returnContainer = "List";
                    }
                } else if (operation.returnType.startsWith("Map")) {
                    String rt = operation.returnType;
                    int end = rt.lastIndexOf(">");
                    if (end > 0) {
                        operation.returnType = rt.substring("Map<".length(), end).split(",")[1].trim();
                        operation.returnContainer = "Map";
                    }
                } else if (operation.returnType.startsWith("Set")) {
                    String rt = operation.returnType;
                    int end = rt.lastIndexOf(">");
                    if (end > 0) {
                        operation.returnType = rt.substring("Set<".length(), end).trim();
                        operation.returnContainer = "Set";
                    }
                }
            }
        }
        return objs;
    }

    @Override
    public String toApiName(String name) {
        if (name.length() == 0) {
            return "DefaultApi";
        }
        name = sanitizeName(name);
        return camelize(name) + "Api";
    }

    @Override
    public String apiFilename(String templateName, String tag) {

        String result = super.apiFilename(templateName, tag);

        if (templateName.endsWith("Impl.mustache")) {
        	result = renamePackage(result, "ServiceImpl.java", "swagger.codegen.jaxrs.impl.source", "io.swagger.impl");
        } else if (templateName.endsWith("Factory.mustache")) {
            result = renamePackage(result, "ServiceFactory.java", "swagger.codegen.jaxrs.factory.source", "io.swagger.factory");
        } else if (templateName.endsWith("Service.mustache")) {
            int ix = result.lastIndexOf('.');
            result = result.substring(0, ix) + "Service.java";
        } else if (templateName.endsWith("api.mustache")){
        	result = renamePackage(result, ".java", "swagger.codegen.jaxrs.resources", "io.swagger.api");
        } else if(templateName.endsWith("Dao.mustache")){
        	result = renamePackage(result, "DAO.java", "swagger.codegen.jaxrs.dbpackage", null);
        }

        return result;
    }

	private String renamePackage(String result, String fileEnding, String packageProperty, String defaultPackageProperty) {
		int ix = result.lastIndexOf('/');
		String begining = result.substring(0, ix);
		String end = result.substring(ix, result.length() - 5) + fileEnding; 

		String output = System.getProperty(packageProperty, defaultPackageProperty);
		if (output != null) {
			ix = end.lastIndexOf('\\');
			end = end.substring(ix, end.length());
		    result = rootSourceFolder() +  output.replace('.', File.separatorChar) + end;
		} else {
			result = begining + end;
		}
		return result;
	}

    public boolean shouldOverwrite(String filename) {
        return super.shouldOverwrite(filename) && !filename.endsWith("ServiceImpl.java") && !filename.endsWith("ServiceFactory.java");
    }
}
