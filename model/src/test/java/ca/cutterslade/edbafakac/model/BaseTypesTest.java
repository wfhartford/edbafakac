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
    final TypeValue type = baseType.getValue();
    assertNotNull(type);
    assertEquals(baseType.getKey(), type.getKey());
    assertNotNull(type.getName(RetrieveMode.READ_ONLY));
    assertEquals(getName(baseType), type.getName(RetrieveMode.READ_ONLY).getBaseValue());
    final ListValue fields = type.getTypeFields(RetrieveMode.READ_ONLY);
    assertNotNull(fields);
    assertEquals(BaseType.FIELD.getValue(), fields.getValueType(RetrieveMode.READ_ONLY));
    assertTrue(0 < fields.getSize());
    final long size = fields.getSize();
    for (long i = 0; i < size; i++) {
      final FieldValue field = (FieldValue) fields.get(i);
      assertNotNull(field);
      assertNotNull(field.getFieldType(RetrieveMode.READ_ONLY));
      assertNotNull(field.getName(RetrieveMode.READ_ONLY));
    }
  }

  public static String getName(final Enum<?> type) {
    return type.getDeclaringClass().getSimpleName() + '.' + type.toString();
  }
}
