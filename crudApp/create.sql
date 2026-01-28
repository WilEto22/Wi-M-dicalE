
    create table appointments (
        appointment_date_time datetime(6) not null,
        created_at datetime(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        patient_id bigint not null,
        updated_at datetime(6),
        reason varchar(500),
        doctor_notes varchar(2000),
        status enum ('CANCELLED','COMPLETED','CONFIRMED','PENDING') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability (
        end_time time(6) not null,
        is_active bit not null,
        slot_duration_minutes integer not null,
        start_time time(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        day_of_week enum ('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability_exceptions (
        created_at date not null,
        exception_date date not null,
        is_active bit not null,
        is_available bit not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        end_time varchar(255),
        reason varchar(255),
        start_time varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table patients (
        age integer not null,
        blood_type varchar(3),
        is_active bit,
        last_visit date,
        id bigint not null auto_increment,
        user_id bigint,
        emergency_phone varchar(20),
        phone_number varchar(20),
        insurance_number varchar(50),
        emergency_contact varchar(100),
        name varchar(100) not null,
        allergies varchar(1000),
        medical_history varchar(5000),
        address varchar(255) not null,
        email varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table refresh_tokens (
        expiry_date datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users (
        date_of_birth date,
        id bigint not null auto_increment,
        phone_number varchar(20),
        email varchar(255),
        full_name varchar(255),
        password varchar(255) not null,
        roles varchar(255),
        username varchar(255) not null,
        medical_specialty enum ('ALLERGOLOGIE','ANATOMOPATHOLOGIE','ANESTHESIE_REANIMATION','AUDIOLOGIE','BIOLOGIE_MEDICALE','CARDIOLOGIE','CHIRURGIE_CARDIAQUE','CHIRURGIE_GENERALE','CHIRURGIE_ORTHOPEDIQUE','CHIRURGIE_PEDIATRIQUE','CHIRURGIE_PLASTIQUE','CHIRURGIE_VISCERALE','DERMATOLOGIE','ENDOCRINOLOGIE','GASTRO_ENTEROLOGIE','GERIATRIE','HEMATOLOGIE','INFECTIOLOGIE','MEDECINE_GENERALE','MEDECINE_LEGALE','MEDECINE_NUCLEAIRE','MEDECINE_PHYSIQUE_READAPTATION','MEDECINE_SPORT','MEDECINE_TRAVAIL','MEDECINE_URGENCE','NEPHROLOGIE','NEUROCHIRURGIE','NEUROLOGIE','ONCOLOGIE','OPHTALMOLOGIE','ORL','PEDIATRIE','PNEUMOLOGIE','PSYCHIATRIE','RADIOLOGIE','RHUMATOLOGIE','SANTE_PUBLIQUE'),
        user_type enum ('DOCTOR','PATIENT') not null,
        primary key (id)
    ) engine=InnoDB;

    alter table patients 
       add constraint UKjmj4vn423f1ecw04a2mcv470e unique (phone_number);

    alter table patients 
       add constraint UK898ikjlngeolpml0vt2hrer1r unique (insurance_number);

    alter table patients 
       add constraint UKa370hmxgv0l5c9panryr1ji7d unique (email);

    alter table refresh_tokens 
       add constraint UKghpmfn23vmxfu3spu3lfg4r2d unique (token);

    alter table users 
       add constraint UK9q63snka3mdh91as4io72espi unique (phone_number);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table users 
       add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

    alter table appointments 
       add constraint FK6u6s6egu60m2cbdjno44jbipa 
       foreign key (doctor_id) 
       references users (id);

    alter table appointments 
       add constraint FKopb2h9yhin1rb4dqote8bws6w 
       foreign key (patient_id) 
       references users (id);

    alter table doctor_availability 
       add constraint FK1mfkjo7s5ct426dm5a2hyiur0 
       foreign key (doctor_id) 
       references users (id);

    alter table doctor_availability_exceptions 
       add constraint FKek67xqf1je6otukxhy66vjcy2 
       foreign key (doctor_id) 
       references users (id);

    alter table patients 
       add constraint FKuwca24wcd1tg6pjex8lmc0y7 
       foreign key (user_id) 
       references users (id);

    alter table refresh_tokens 
       add constraint FK1lih5y2npsf8u5o3vhdb9y0os 
       foreign key (user_id) 
       references users (id);

    create table appointments (
        appointment_date_time datetime(6) not null,
        created_at datetime(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        patient_id bigint not null,
        updated_at datetime(6),
        reason varchar(500),
        doctor_notes varchar(2000),
        status enum ('CANCELLED','COMPLETED','CONFIRMED','PENDING') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability (
        end_time time(6) not null,
        is_active bit not null,
        slot_duration_minutes integer not null,
        start_time time(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        day_of_week enum ('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability_exceptions (
        created_at date not null,
        exception_date date not null,
        is_active bit not null,
        is_available bit not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        end_time varchar(255),
        reason varchar(255),
        start_time varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table patients (
        age integer not null,
        blood_type varchar(3),
        is_active bit,
        last_visit date,
        id bigint not null auto_increment,
        user_id bigint,
        emergency_phone varchar(20),
        phone_number varchar(20),
        insurance_number varchar(50),
        emergency_contact varchar(100),
        name varchar(100) not null,
        allergies varchar(1000),
        medical_history varchar(5000),
        address varchar(255) not null,
        email varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table refresh_tokens (
        expiry_date datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users (
        date_of_birth date,
        id bigint not null auto_increment,
        phone_number varchar(20),
        email varchar(255),
        full_name varchar(255),
        password varchar(255) not null,
        roles varchar(255),
        username varchar(255) not null,
        medical_specialty enum ('ALLERGOLOGIE','ANATOMOPATHOLOGIE','ANESTHESIE_REANIMATION','AUDIOLOGIE','BIOLOGIE_MEDICALE','CARDIOLOGIE','CHIRURGIE_CARDIAQUE','CHIRURGIE_GENERALE','CHIRURGIE_ORTHOPEDIQUE','CHIRURGIE_PEDIATRIQUE','CHIRURGIE_PLASTIQUE','CHIRURGIE_VISCERALE','DERMATOLOGIE','ENDOCRINOLOGIE','GASTRO_ENTEROLOGIE','GERIATRIE','HEMATOLOGIE','INFECTIOLOGIE','MEDECINE_GENERALE','MEDECINE_LEGALE','MEDECINE_NUCLEAIRE','MEDECINE_PHYSIQUE_READAPTATION','MEDECINE_SPORT','MEDECINE_TRAVAIL','MEDECINE_URGENCE','NEPHROLOGIE','NEUROCHIRURGIE','NEUROLOGIE','ONCOLOGIE','OPHTALMOLOGIE','ORL','PEDIATRIE','PNEUMOLOGIE','PSYCHIATRIE','RADIOLOGIE','RHUMATOLOGIE','SANTE_PUBLIQUE'),
        user_type enum ('DOCTOR','PATIENT') not null,
        primary key (id)
    ) engine=InnoDB;

    alter table patients 
       add constraint UKjmj4vn423f1ecw04a2mcv470e unique (phone_number);

    alter table patients 
       add constraint UK898ikjlngeolpml0vt2hrer1r unique (insurance_number);

    alter table patients 
       add constraint UKa370hmxgv0l5c9panryr1ji7d unique (email);

    alter table refresh_tokens 
       add constraint UKghpmfn23vmxfu3spu3lfg4r2d unique (token);

    alter table users 
       add constraint UK9q63snka3mdh91as4io72espi unique (phone_number);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table users 
       add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

    alter table appointments 
       add constraint FK6u6s6egu60m2cbdjno44jbipa 
       foreign key (doctor_id) 
       references users (id);

    alter table appointments 
       add constraint FKopb2h9yhin1rb4dqote8bws6w 
       foreign key (patient_id) 
       references users (id);

    alter table doctor_availability 
       add constraint FK1mfkjo7s5ct426dm5a2hyiur0 
       foreign key (doctor_id) 
       references users (id);

    alter table doctor_availability_exceptions 
       add constraint FKek67xqf1je6otukxhy66vjcy2 
       foreign key (doctor_id) 
       references users (id);

    alter table patients 
       add constraint FKuwca24wcd1tg6pjex8lmc0y7 
       foreign key (user_id) 
       references users (id);

    alter table refresh_tokens 
       add constraint FK1lih5y2npsf8u5o3vhdb9y0os 
       foreign key (user_id) 
       references users (id);

    create table appointments (
        appointment_date_time datetime(6) not null,
        created_at datetime(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        patient_id bigint not null,
        updated_at datetime(6),
        reason varchar(500),
        doctor_notes varchar(2000),
        status enum ('CANCELLED','COMPLETED','CONFIRMED','PENDING') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability (
        end_time time(6) not null,
        is_active bit not null,
        slot_duration_minutes integer not null,
        start_time time(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        day_of_week enum ('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability_exceptions (
        created_at date not null,
        exception_date date not null,
        is_active bit not null,
        is_available bit not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        end_time varchar(255),
        reason varchar(255),
        start_time varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table patients (
        age integer not null,
        blood_type varchar(3),
        is_active bit,
        last_visit date,
        id bigint not null auto_increment,
        user_id bigint,
        emergency_phone varchar(20),
        phone_number varchar(20),
        insurance_number varchar(50),
        emergency_contact varchar(100),
        name varchar(100) not null,
        allergies varchar(1000),
        medical_history varchar(5000),
        address varchar(255) not null,
        email varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table refresh_tokens (
        expiry_date datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users (
        date_of_birth date,
        id bigint not null auto_increment,
        phone_number varchar(20),
        email varchar(255),
        full_name varchar(255),
        password varchar(255) not null,
        roles varchar(255),
        username varchar(255) not null,
        medical_specialty enum ('ALLERGOLOGIE','ANATOMOPATHOLOGIE','ANESTHESIE_REANIMATION','AUDIOLOGIE','BIOLOGIE_MEDICALE','CARDIOLOGIE','CHIRURGIE_CARDIAQUE','CHIRURGIE_GENERALE','CHIRURGIE_ORTHOPEDIQUE','CHIRURGIE_PEDIATRIQUE','CHIRURGIE_PLASTIQUE','CHIRURGIE_VISCERALE','DERMATOLOGIE','ENDOCRINOLOGIE','GASTRO_ENTEROLOGIE','GERIATRIE','HEMATOLOGIE','INFECTIOLOGIE','MEDECINE_GENERALE','MEDECINE_LEGALE','MEDECINE_NUCLEAIRE','MEDECINE_PHYSIQUE_READAPTATION','MEDECINE_SPORT','MEDECINE_TRAVAIL','MEDECINE_URGENCE','NEPHROLOGIE','NEUROCHIRURGIE','NEUROLOGIE','ONCOLOGIE','OPHTALMOLOGIE','ORL','PEDIATRIE','PNEUMOLOGIE','PSYCHIATRIE','RADIOLOGIE','RHUMATOLOGIE','SANTE_PUBLIQUE'),
        user_type enum ('DOCTOR','PATIENT') not null,
        primary key (id)
    ) engine=InnoDB;

    alter table patients 
       add constraint UKjmj4vn423f1ecw04a2mcv470e unique (phone_number);

    alter table patients 
       add constraint UK898ikjlngeolpml0vt2hrer1r unique (insurance_number);

    alter table patients 
       add constraint UKa370hmxgv0l5c9panryr1ji7d unique (email);

    alter table refresh_tokens 
       add constraint UKghpmfn23vmxfu3spu3lfg4r2d unique (token);

    alter table users 
       add constraint UK9q63snka3mdh91as4io72espi unique (phone_number);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table users 
       add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

    alter table appointments 
       add constraint FK6u6s6egu60m2cbdjno44jbipa 
       foreign key (doctor_id) 
       references users (id);

    alter table appointments 
       add constraint FKopb2h9yhin1rb4dqote8bws6w 
       foreign key (patient_id) 
       references users (id);

    alter table doctor_availability 
       add constraint FK1mfkjo7s5ct426dm5a2hyiur0 
       foreign key (doctor_id) 
       references users (id);

    alter table doctor_availability_exceptions 
       add constraint FKek67xqf1je6otukxhy66vjcy2 
       foreign key (doctor_id) 
       references users (id);

    alter table patients 
       add constraint FKuwca24wcd1tg6pjex8lmc0y7 
       foreign key (user_id) 
       references users (id);

    alter table refresh_tokens 
       add constraint FK1lih5y2npsf8u5o3vhdb9y0os 
       foreign key (user_id) 
       references users (id);

    create table appointments (
        appointment_date_time datetime(6) not null,
        created_at datetime(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        patient_id bigint not null,
        updated_at datetime(6),
        reason varchar(500),
        doctor_notes varchar(2000),
        status enum ('CANCELLED','COMPLETED','CONFIRMED','PENDING') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability (
        end_time time(6) not null,
        is_active bit not null,
        slot_duration_minutes integer not null,
        start_time time(6) not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        day_of_week enum ('FRIDAY','MONDAY','SATURDAY','SUNDAY','THURSDAY','TUESDAY','WEDNESDAY') not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_availability_exceptions (
        created_at date not null,
        exception_date date not null,
        is_active bit not null,
        is_available bit not null,
        doctor_id bigint not null,
        id bigint not null auto_increment,
        end_time varchar(255),
        reason varchar(255),
        start_time varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table patients (
        age integer not null,
        blood_type varchar(3),
        is_active bit,
        last_visit date,
        id bigint not null auto_increment,
        user_id bigint,
        emergency_phone varchar(20),
        phone_number varchar(20),
        insurance_number varchar(50),
        emergency_contact varchar(100),
        name varchar(100) not null,
        allergies varchar(1000),
        medical_history varchar(5000),
        address varchar(255) not null,
        email varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table refresh_tokens (
        expiry_date datetime(6) not null,
        id bigint not null auto_increment,
        user_id bigint not null,
        token varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table users (
        date_of_birth date,
        id bigint not null auto_increment,
        phone_number varchar(20),
        email varchar(255),
        full_name varchar(255),
        password varchar(255) not null,
        roles varchar(255),
        username varchar(255) not null,
        medical_specialty enum ('ALLERGOLOGIE','ANATOMOPATHOLOGIE','ANESTHESIE_REANIMATION','AUDIOLOGIE','BIOLOGIE_MEDICALE','CARDIOLOGIE','CHIRURGIE_CARDIAQUE','CHIRURGIE_GENERALE','CHIRURGIE_ORTHOPEDIQUE','CHIRURGIE_PEDIATRIQUE','CHIRURGIE_PLASTIQUE','CHIRURGIE_VISCERALE','DERMATOLOGIE','ENDOCRINOLOGIE','GASTRO_ENTEROLOGIE','GERIATRIE','HEMATOLOGIE','INFECTIOLOGIE','MEDECINE_GENERALE','MEDECINE_LEGALE','MEDECINE_NUCLEAIRE','MEDECINE_PHYSIQUE_READAPTATION','MEDECINE_SPORT','MEDECINE_TRAVAIL','MEDECINE_URGENCE','NEPHROLOGIE','NEUROCHIRURGIE','NEUROLOGIE','ONCOLOGIE','OPHTALMOLOGIE','ORL','PEDIATRIE','PNEUMOLOGIE','PSYCHIATRIE','RADIOLOGIE','RHUMATOLOGIE','SANTE_PUBLIQUE'),
        user_type enum ('DOCTOR','PATIENT') not null,
        primary key (id)
    ) engine=InnoDB;

    alter table patients 
       add constraint UKjmj4vn423f1ecw04a2mcv470e unique (phone_number);

    alter table patients 
       add constraint UK898ikjlngeolpml0vt2hrer1r unique (insurance_number);

    alter table patients 
       add constraint UKa370hmxgv0l5c9panryr1ji7d unique (email);

    alter table refresh_tokens 
       add constraint UKghpmfn23vmxfu3spu3lfg4r2d unique (token);

    alter table users 
       add constraint UK9q63snka3mdh91as4io72espi unique (phone_number);

    alter table users 
       add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email);

    alter table users 
       add constraint UKr43af9ap4edm43mmtq01oddj6 unique (username);

    alter table appointments 
       add constraint FK6u6s6egu60m2cbdjno44jbipa 
       foreign key (doctor_id) 
       references users (id);

    alter table appointments 
       add constraint FKopb2h9yhin1rb4dqote8bws6w 
       foreign key (patient_id) 
       references users (id);

    alter table doctor_availability 
       add constraint FK1mfkjo7s5ct426dm5a2hyiur0 
       foreign key (doctor_id) 
       references users (id);

    alter table doctor_availability_exceptions 
       add constraint FKek67xqf1je6otukxhy66vjcy2 
       foreign key (doctor_id) 
       references users (id);

    alter table patients 
       add constraint FKuwca24wcd1tg6pjex8lmc0y7 
       foreign key (user_id) 
       references users (id);

    alter table refresh_tokens 
       add constraint FK1lih5y2npsf8u5o3vhdb9y0os 
       foreign key (user_id) 
       references users (id);
