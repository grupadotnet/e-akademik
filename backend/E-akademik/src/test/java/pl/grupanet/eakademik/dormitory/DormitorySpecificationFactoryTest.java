    package pl.grupanet.eakademik.dormitory;

    import org.commons.DormitoryId;
    import org.commons.Name;
    import org.junit.jupiter.api.Test;
    import pl.grupanet.eakademik.dormitory.slots.SlotsRoomSpecification;

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.mock;

    class DormitorySpecificationFactoryTest {

        @Test
        void shouldCreateDormitorySpecificationWithDefaultRoomSpecification() {
            DormitoryId dormitoryId = new DormitoryId(new Name("PK"), new Name("Dor1"));
            DormitorySpecificationFactory factory = new DormitorySpecificationFactory(
                    dormitoryId.getUniversityName().toString(),
                    dormitoryId.getDormitoryName().toString()
            );

            DormitorySpecification specification = factory.create();

            assertEquals(dormitoryId, specification.dormitoryId());
            assertEquals(SlotsRoomSpecification.defaultRoomSpecification, specification.slotsRoomSpecification());
        }

        @Test
        void shouldThrowExceptionWhenDormitoryIdIsNull() {
            assertThrows(NullPointerException.class, () -> {
                new DormitorySpecificationFactory(null, null);
            });
            assertThrows(NullPointerException.class, () -> {
                new DormitorySpecificationFactory("PK", null);
            });
            assertThrows(NullPointerException.class, () -> {
                new DormitorySpecificationFactory(null, "Dor1");
            });
        }

        @Test
        void shouldCreateSpecificationWithOverriddenRoomSpecification() {
            DormitoryId dormitoryId = new DormitoryId(new Name("PK"), new Name("Dor1"));
            SlotsRoomSpecification customRoomSpecification = mock(SlotsRoomSpecification.class);

            DormitorySpecificationFactory factory = new DormitorySpecificationFactory(
                    dormitoryId.getUniversityName().toString(),
                    dormitoryId.getDormitoryName().toString()
            ) {
                @Override
                public SlotsRoomSpecification buildSlotsRoomSpecification() {
                    return customRoomSpecification;
                }
            };

            DormitorySpecification specification = factory.create();

            assertEquals(dormitoryId, specification.dormitoryId());
            assertEquals(customRoomSpecification, specification.slotsRoomSpecification());
        }
    }