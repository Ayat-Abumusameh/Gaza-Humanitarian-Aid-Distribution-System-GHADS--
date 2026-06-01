    package models;

    import java.time.LocalDate;
    import javax.persistence.*;

    @Entity
    @Table(name = "aid_distribution")
    public class AidDistribution {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "distribution_id")
        private int distributionId;

        @ManyToOne
        @JoinColumn(name = "family_id")
        private Family family;

        @ManyToOne
        @JoinColumn(name = "org_id")
        private Organization organization;

        @Column(name = "distributed_by")
        private int distributedBy;

        @Column(name = "distribution_date")
        @Convert(converter = config.LocalDateConverter.class)
        private LocalDate distributionDate;

        public AidDistribution() {}

        public AidDistribution(Family family, Organization organization, int distributedBy, LocalDate distributionDate) {
            this.family = family;
            this.organization = organization;
            this.distributedBy = distributedBy;
            this.distributionDate = distributionDate;
        }

        public int getDistributionId() { 
            return distributionId; 
        }
        public void setDistributionId(int distributionId) { 
            this.distributionId = distributionId;
        }

        public Family getFamily() {
            return family; 
        }
        public void setFamily(Family family) {
            this.family = family;
        }

        public Organization getOrganization() { 
            return organization;
        }
        public void setOrganization(Organization organization) {
            this.organization = organization;
        }

        public int getDistributedBy() {
            return distributedBy; 
        }
        public void setDistributedBy(int distributedBy) { 
            this.distributedBy = distributedBy;
        }

        public LocalDate getDistributionDate() { 
            return distributionDate; 
        }
        public void setDistributionDate(LocalDate distributionDate) { 
            this.distributionDate = distributionDate; 
        }

        public int getFamilyId() { 
            return family.getFamilyId(); 
        }
        public int getOrgId() {
            return organization.getOrgId(); 
        }

        @Override
        public String toString() {
            return "Dist ID: " + distributionId + " -> Family ID: " + getFamilyId() + " By: " + distributedBy + " Date: " + distributionDate;
        }

        public static AidDistribution createHeader(String label) {
            Family dummyFamily = new Family();
            dummyFamily.setHouseholdName(label);
            return new AidDistribution(dummyFamily, null, 0, null);
        }
    }