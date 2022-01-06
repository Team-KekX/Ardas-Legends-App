const { SlashCommandBuilder } = require('@discordjs/builders');
const fs = require('fs');

module.exports = {
    data: new SlashCommandBuilder()
        .setName('dice')
        .setDescription('Throws a dice')
        .addSubcommand(subcommand =>
            subcommand
                .setName('normal')
                .setDescription('Rolls a normal dice roll')
                .addIntegerOption(option =>
                    option.setName('times')
                        .setDescription('how many times to throw the dice')
                        .setRequired(true))
                .addIntegerOption(option =>
                    option.setName('roll')
                        .setDescription('the type of dice to be rolled')
                        .setRequired(true))
        )
        .addSubcommand( subcommand =>
            subcommand
                .setName('skill')
                .setDescription('Rolls a skill roll')
                .addStringOption(option =>
                    option
                        .setName('name')
                        .setDescription('Name of the character')
                        .setRequired(true)),
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const path = './commands/subcommands/';
        const files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('dice_')));
        const commands = {};
        for (const file of files){
            const name = file.split('dice_')[1].slice(0,-3);
            commands[name]= require('./subcommands/'+file);
        }
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};