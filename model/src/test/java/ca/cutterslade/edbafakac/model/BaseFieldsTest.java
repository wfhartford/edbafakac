package ca.cutterslade.edbafakac.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
@RunWith(Parameterized.class)
public class BaseFieldsTest {

  @Parameters
  public static Collection<Object[]> getParameters() {
    final ImmutableList.Builder<Object[]> builder = ImmutableList.builder();
    for (final BaseField field : BaseField.values()) {
      builder.add(new Object[]{ field });
    }
    return builder.build();
  }

  private final BaseField baseField;

  public BaseFieldsTest(final BaseField baseField) {
    this.baseField = baseField;
  }

  @Test
  public void testField() {
    final FieldValue field = baseField.getValue();
    assertNotNull(field);
    assertNotNull(field.getName(true));
    assertEquals(BaseTypesTest.getName(baseField), field.getName(true).getBaseValue());
    assertNotNull(field.getFieldType(true));
    assertNotNull(BaseType.getBaseType(field.getFieldType(true).getKey()));
  }
}
