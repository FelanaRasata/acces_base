package base;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;

public class Reflect implements Serializable{

    // construire un objet
    // @param classe classe de l'objet à créer
    public Object createObject(Class<?> classe) throws Exception{

        Constructor<?> constructor = classe.getConstructor();
        Object object = constructor.newInstance();
        
        return object;
    
    }

    
    // executer une fonction setter
    // @param object l'objet appelant
    // @param attribut l'attribut à transformer
    // @param value la valeur à ajouter
    public void executeSetter(Object object, String attribut, String value) throws Exception {
    
        Class<?> objectClass = object.getClass();

        Field field = this.getField(objectClass,attribut);
        Class<?> attributClass = field.getType();

        Object attributValue = this.convertValue(attributClass, value);

        Method method = this.getSetter(objectClass, attribut, attributClass);
        method.invoke(object, attributValue);

    }

    
    // Transformer un string en une classe donnée
    // @param valueClass le classe finale
    // @param value la valeur à transformer
    public Object convertValue( Class<?> valueClass, String value){
        
        Object object = value;
        String finalValue = valueClass.getSimpleName();
        
        if (finalValue.equalsIgnoreCase("int")) {
        
            object = Integer.parseInt(value);
        
        } else if (finalValue.equalsIgnoreCase("double")) {
        
            object = Double.parseDouble(value);
        
        } else if (finalValue.equalsIgnoreCase("float")) {
        
            object = Float.parseFloat(value);
        
        } else if (finalValue.equalsIgnoreCase("Date")) {
            
            object = Date.valueOf(value);
        
        } else if (finalValue.equalsIgnoreCase("Time")) {
            
            object = Time.valueOf(value);
        
        }
        
        return object;
    
    }


    
    // @param classe la classe ou se trouve l'attribut
    // @param attribut
    public Method getSetter(Class<?> classe, String attribut, Class classAtribut) throws Exception{
        
        Tool tool = new Tool();

        String nameFunction = "set" + tool.toUpperFirst(attribut);

        Class<?>[] args = new Class[1];
        args[0] = classAtribut;

        Method method = classe.getMethod(nameFunction, args);

        return method;

    }

    public Object executeGetter (Object object, String attribut) throws Exception{

        Class<?> objectClass = object.getClass();
        
        Method method = this.getGetter(objectClass,attribut);
        Object objectValue = method.invoke(object);
        
        return objectValue;
    
    }


    public Method getGetter(Class<?> classe, String attribut) throws Exception{

        Tool tool = new Tool();

        String nameFunction = "get" + tool.toUpperFirst(attribut);
        Method method = classe.getMethod(nameFunction);

        return method;

    }


    public ArrayList<Field> getFields(Class classe){

        Field[] fields = classe.getDeclaredFields();
        ArrayList<Field> fieldList = new ArrayList();

        for(int i=0; i<fields.length; i++){

            fieldList.add(fields[i]);

        }

        boolean test = true;
        Class classField = classe;

        while(test){

            try{

                Class superClass = classField.getSuperclass();

                Field[] tempFields = superClass.getDeclaredFields();
                for(int i=0; i<tempFields.length; i++){

                    fieldList.add(tempFields[i]);

                }

                classField = superClass;

            }catch(Exception e){

                test = false;

            }

        }

        return fieldList;

    }
    
    public Field getField(Class classe, String attribut) throws Exception{
    
        ArrayList<Field> fieldList = this.getFields(classe);
    
        for(int i=0; i<fieldList.size(); i++){
    
            if(fieldList.get(i).getName().equalsIgnoreCase(attribut)){
    
                return fieldList.get(i);
    
            }
    
        }
    
        throw new Exception("Field not in class");
    
    }


    //PROJET FRAMEWORK

    public String[] getElement(String url){
    
        url = url.replaceAll("/", "");
        url = url.replaceAll(".do", "");
    
        String[] element = url.split("-");
    
        return element;
    
    }
    

    //Créer le lien pour le view finel
    public String createView(String[] element ){
       
        String url = "View/" + element[0] + "/" + element[1] + ".jsp";
       
        return url;
    
    }
    

    public Method getMethod(String className, String functionName, Class[] args) throws Exception {
    
        Class classe = Class.forName(className);
        Method method = classe.getMethod(functionName, args);
    
        return method;
    
    }
    
    public void setAttribut(Object object, HttpServletRequest request) throws Exception{

        Class<?> classe = object.getClass();

        Enumeration<String> attribut = request.getParameterNames();

        while(true){

            try{

                String parameter = attribut.nextElement();
                String value = request.getParameter(parameter);

                this.executeSetter(object, parameter, value);

            }catch(NoSuchElementException e){

                break;

            }catch(Exception e){}

        }

    }
    
}
