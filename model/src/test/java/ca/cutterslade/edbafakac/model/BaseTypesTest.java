package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BaseTypesTest {

  @Test
  public void testBooleanType() {
    final BaseType baseType = BaseType.BOOLEAN;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testDateType() {
    final BaseType baseType = BaseType.DATE;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testDecimalType() {
    final BaseType baseType = BaseType.DECIMAL;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testIntegerType() {
    final BaseType baseType = BaseType.INTEGER;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testListType() {
    final BaseType baseType = BaseType.LIST;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testRawType() {
    final BaseType baseType = BaseType.RAW;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testStringType() {
    final BaseType baseType = BaseType.STRING;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testFieldType() {
    final BaseType baseType = BaseType.FIELD;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  @Test
  public void testTypeType() {
    final BaseType baseType = BaseType.TYPE;
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName());
    assertEquals(getName(baseType), type.getName().getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName());
    }
  }

  private String getName(final BaseType type) {
    return BaseType.class.getSimpleName() + '.' + type.toString();
  }
}
