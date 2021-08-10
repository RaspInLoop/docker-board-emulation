/*******************************************************************************
 * Copyright 2018 RaspInLoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
//
// Ce fichier a �t� g�n�r� par l'impl�mentation de r�f�rence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apport�e � ce fichier sera perdue lors de la recompilation du sch�ma source. 
// G�n�r� le : 2015.03.16 � 07:28:31 AM CET 
//


package org.raspinloop.emulator.fmi.modeldescription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import lombok.Generated;


/**
 * <p>Classe Java pour fmi2VariableDependency complex type.
 * 
 * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="fmi2VariableDependency">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="Unknown">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="index" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *                 &lt;attribute name="dependencies">
 *                   &lt;simpleType>
 *                     &lt;list itemType="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="dependenciesKind">
 *                   &lt;simpleType>
 *                     &lt;list>
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
 *                           &lt;enumeration value="dependent"/>
 *                           &lt;enumeration value="constant"/>
 *                           &lt;enumeration value="fixed"/>
 *                           &lt;enumeration value="tunable"/>
 *                           &lt;enumeration value="discrete"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/list>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fmi2VariableDependency", propOrder = {
    "unknown"
})
@Generated
public class Fmi2VariableDependency {

    @XmlElement(name = "Unknown", required = true)
    protected List<Fmi2VariableDependency.Unknown> unknown;

    /**
     * Gets the value of the unknown property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unknown property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnknown().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Fmi2VariableDependency.Unknown }
     * 
     * 
     */
    public List<Fmi2VariableDependency.Unknown> getUnknown() {
        if (unknown == null) {
            unknown = new ArrayList<>();
        }
        return this.unknown;
    }


    /**
     * <p>Classe Java pour anonymous complex type.
     * 
     * <p>Le fragment de sch�ma suivant indique le contenu attendu figurant dans cette classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="index" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
     *       &lt;attribute name="dependencies">
     *         &lt;simpleType>
     *           &lt;list itemType="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="dependenciesKind">
     *         &lt;simpleType>
     *           &lt;list>
     *             &lt;simpleType>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}normalizedString">
     *                 &lt;enumeration value="dependent"/>
     *                 &lt;enumeration value="constant"/>
     *                 &lt;enumeration value="fixed"/>
     *                 &lt;enumeration value="tunable"/>
     *                 &lt;enumeration value="discrete"/>
     *               &lt;/restriction>
     *             &lt;/simpleType>
     *           &lt;/list>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    @Generated
    public static class Unknown {

        @XmlAttribute(name = "index", required = true)
        @XmlSchemaType(name = "unsignedInt")
        protected long index;
        @XmlAttribute(name = "dependencies")
        protected List<Long> dependencies;
        @XmlAttribute(name = "dependenciesKind")
        protected List<String> dependenciesKind;

        /**
         * Obtient la valeur de la propri�t� index.
         * 
         */
        public long getIndex() {
            return index;
        }

        /**
         * D�finit la valeur de la propri�t� index.
         * 
         */
        public void setIndex(long value) {
            this.index = value;
        }

        /**
         * Gets the value of the dependencies property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dependencies property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDependencies().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Long }
         * 
         * 
         */
        public List<Long> getDependencies() {
            if (dependencies == null) {
                dependencies = new ArrayList<>();
            }
            return this.dependencies;
        }

        /**
         * Gets the value of the dependenciesKind property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the dependenciesKind property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDependenciesKind().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getDependenciesKind() {
            if (dependenciesKind == null) {
                dependenciesKind = new ArrayList<>();
            }
            return this.dependenciesKind;
        }

    }

}
