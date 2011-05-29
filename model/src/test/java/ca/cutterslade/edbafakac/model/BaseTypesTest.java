package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@RunWith(Parameterized.class)
public class BaseTypesTest {

  @Parameters
  public static Collection<Object[]> getParameters() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();
    for (final BaseType type : BaseType.values()) {
      builder.add(new Object[]{ type });
    }
    return builder.build();
  }

  private final BaseType baseType;

  public BaseTypesTest(final BaseType baseType) {
    this.baseType = baseType;
  }

  @Test
  public void testType() {
    final TypeValue type = baseType.getType();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName(true));
    assertEquals(getName(baseType), type.getName(true).getBaseValue());
    final ListValue fields = type.getTypeFields(true);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getType(), fields.getValueType(true));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(true));
      assertNotNull(field.getName(true));
    }
  }

  public static String getName(final Enum<?> type) {
    return type.getDeclaringClass().getSimpleName() + '.' + type.toString();
  }
}
