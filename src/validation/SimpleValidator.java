///*
// * @(#)SimpleValidator.java 5/19/2013
// *
// * Copyright 2002 - 2013 JIDE Software Inc. All rights reserved.
// */
//
//package validation;
//
//import util.ReflectionUtils;
//
//public class SimpleValidator implements Validator {
//    private Object _validator;
//
//    public SimpleValidator(Object validator) {
//        _validator = validator;
//    }
//
//    @Override  public ValidationEvent call(ValidationObject param) {
//        try {
//            Object valid = ReflectionUtils.callAny(_validator, "isValid", new Class[]{String.class}, new Object[]{param.getNewValue().toString()});
//            String name = _validator.getClass().getSimpleName();
//            int index = name.indexOf("Validator");
//            if (Boolean.FALSE.equals(valid)) return ValidationEvent.OK;
//            return new ValidationEvent(ValidationEvent.VALIDATION_ERROR, 0, ValidationEvent.FailBehavior.PERSIST, 
//            				"Invalid " + name.substring(0, index) + "!");
//        }
//        catch (Exception e) {     return ValidationEvent.UNKNOWN;      }
//    }
//}
