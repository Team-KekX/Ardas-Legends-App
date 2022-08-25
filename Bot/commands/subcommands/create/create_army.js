const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {CREATE} = require('../../../configs/embed_thumbnails.json');
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        const name = capitalizeFirstLetters(interaction.options.getString('army-name').toLowerCase());
        const claimbuild = capitalizeFirstLetters(interaction.options.getString('claimbuild-name').toLowerCase());
        const units = interaction.options.getString('units');


        const data = {
            executorDiscordId: interaction.member.id,
            name: name,
            armyType: 'ARMY',
            claimBuildName: claimbuild,
            unitString: units
        }

        axios.post(`http://${serverIP}:${serverPort}/api/army/create-army`, data)
            .then(async function(response) {
                const army = response.data;

                let unitString = "";
                for (let i = 0; i < army.units.length; i++) {
                    let unit = army.units[i];
                    let unitsAlive = `${unit.amountAlive}/${unit.count} `;
                    let unitName = `${unit.unitType.unitName}`
                    unitString += unitsAlive + unitName + "\n";
                }

                const replyEmbed = new MessageEmbed()
                    .setTitle(`Army '${name}' created!`)
                    .setDescription(`A new army '${name}' has been raised by ${army.faction.name}!'`)
                    .setColor("GREEN")
                    .setFields(
                        {name: "Name", value: name, inline: true},
                        {name: "Faction", value: army.faction.name, inline: true},
                        {name: "Free Tokens", value: army.freeTokens.toString() + "/30", inline: true},
                        {name: "From Claimbuild", value: claimbuild, inline: true},
                        {name: "Region", value: army.currentRegion, inline: true},
                        {name: "Units", value: unitString, inline: false},
                    )
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error) {
                console.log(error)
                const replyEmbed =new MessageEmbed()
                    .setTitle("Error while creating army")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};
