package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;


import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WizardServiceTest {

    @Mock
    WizardRepository wizardRepository;

    @Mock
    ArtifactRepository artifactRepository;

    @InjectMocks
    WizardService wizardService;

    List<Wizard> wizards;

    @BeforeEach
    void setUp() {
        this.wizards = new ArrayList<>();

        Wizard w1 = new Wizard();
        w1.setId(1);
        w1.setName("Albus Dumbledore");
        this.wizards.add(w1);

        Wizard w2 = new Wizard();
        w2.setId(2);
        w2.setName("Harry Potter");
        this.wizards.add(w2);

        Wizard w3 = new Wizard();
        w3.setId(3);
        w3.setName("Neville Longbottom");
        this.wizards.add(w3);
    }

    @AfterEach
    void tearDown(){
    }

    @Test
    void testFindAllSuccess(){
        // Given
        given(wizardRepository.findAll()).willReturn(this.wizards);

        // When
        List<Wizard> actualWizards = wizardService.findAll();

        // Then
        assertThat(actualWizards.size()).isEqualTo(this.wizards.size());
        verify(wizardRepository, times(1)).findAll();
    }

    @Test
    void testSaveSuccess(){
        // Given
        Wizard newWizard = new Wizard();
        newWizard.setName("Hermione Granger");
        newWizard.setId(4);

        given(wizardRepository.save(newWizard)).willReturn(newWizard);

        // When
        Wizard savedWizard = wizardService.save(newWizard);

        // Then
        assertThat(savedWizard.getId()).isEqualTo(4);
        assertThat(savedWizard.getName()).isEqualTo(newWizard.getName());
        verify(wizardRepository, times(1)).save(newWizard);

    }

    @Test
    void testFindByIdSuccess(){
        // Given
        Wizard w = new Wizard();
        w.setId(1);
        w.setName("Albus Dumbledore");

        given(wizardRepository.findById(1)).willReturn(Optional.of(w));

        // When
        Wizard returnedWizard = wizardService.findById(1);

        // Then
        assertThat(returnedWizard.getId()).isEqualTo(w.getId());
        assertThat(returnedWizard.getName()).isEqualTo(w.getName());
        verify(wizardRepository, times(1)).findById(1);
    }

    @Test
    void testFindByIdNotFound() {
        // Given
        given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(()->{
            Wizard returnedWizard = wizardService.findById(5);
        });

        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 5 :(");
        verify(wizardRepository, times(1)).findById(5);
    }

    @Test
    void testUpdateSuccess(){
        // Given
        Wizard oldWizard = new Wizard();
        oldWizard.setId(1);
        oldWizard.setName("Albus Dumbledore");

        Wizard update = new Wizard();
        oldWizard.setName("Albus Percival Wulfric Brian Dumbledore");

        given(wizardRepository.findById(1)).willReturn(Optional.of(oldWizard));
        given(wizardRepository.save(oldWizard)).willReturn(oldWizard);

        // When
        Wizard updatedWizard = wizardService.update(1, update);

        // Then
        assertThat(updatedWizard.getId()).isEqualTo(1);
        assertThat(updatedWizard.getName()).isEqualTo(update.getName());
        verify(wizardRepository, times(1)).findById(1);
        verify(wizardRepository, times(1)).save(oldWizard);
    }

    @Test
    void testUpdateNotFound(){
        // Given
        Wizard update = new Wizard();
        update.setName("Hermione Weasley");
        update.setId(4);

        given(wizardRepository.findById(4)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () -> {
            wizardService.update(4, update);
        });

        // Then
        verify(wizardRepository, times(1)).findById(4);
    }

    @Test
    void testDeleteSuccess(){
        // Given
        Wizard wizard = new Wizard();
        wizard.setId(1);
        wizard.setName("Harry Potter");

        given(wizardRepository.findById(1)).willReturn(Optional.of(wizard));
        doNothing().when(wizardRepository).deleteById(1);

        // When
        wizardService.delete(1);

        // Then
        verify(wizardRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteNotFound(){
        // Given
        given(wizardRepository.findById(4)).willReturn(Optional.empty());

        // When
        assertThrows(ObjectNotFoundException.class, () ->{
            wizardService.delete(4);
        });

        // Then
        verify(wizardRepository, times(1)).findById(4);
    }

    @Test
    void testAssignArtifactSuccess(){
        // Given
        Artifact a = new Artifact();
        a. setId("1250808601744904192");
        a.setName ("Invisibility Cloak");
        a. setDescription ("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w2 = new Wizard();
        w2. setId (2);
        w2. setName ("Harry Potter");
        w2.addArtifact(a);

        Wizard w3 = new Wizard();
        w3. setId (3);
        w3. setName ("Neville Longbottom");

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.of(w3));

        // When
        this.wizardService.assignArtifact(3, "1250808601744904192");

        // Then
        assertThat(a.getOwner().getId()).isEqualTo(3);
        assertThat(w3.getArtifacts().contains(a));
    }

    @Test
    void testAssignArtifactErrorWithNonExistantWizardId(){
        // Given
        Artifact a = new Artifact();
        a. setId("1250808601744904192");
        a.setName ("Invisibility Cloak");
        a. setDescription ("An invisibility cloak is used to make the wearer invisible.");
        a.setImageUrl("ImageUrl");

        Wizard w2 = new Wizard();
        w2. setId (2);
        w2. setName ("Harry Potter");
        w2.addArtifact(a);



        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(a));
        given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

        // When
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () ->{
            this.wizardService.assignArtifact(3, "1250808601744904192");
        });
        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find wizard with Id 3 :(");
        assertThat(a.getOwner().getId()).isEqualTo(2);
    }

    @Test
    void testAssignArtifactErrorWithNonExistantArtifactId(){
        // Given

        given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

        // When
        Throwable thrown = assertThrows(ObjectNotFoundException.class, () ->{
            this.wizardService.assignArtifact(3, "1250808601744904192");
        });
        // Then
        assertThat(thrown)
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Could not find artifact with Id 1250808601744904192 :(");
    }
}
