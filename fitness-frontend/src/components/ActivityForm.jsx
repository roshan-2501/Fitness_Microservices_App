import React, { useState } from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, TextField, Button } from '@mui/material';



const ActivityForm = ({ onActivityAdded }) => {

    const [activity, setActivity] = useState({
        type: "RUNNING", duration: '', caloriesBurned: '',
        additionalMetrics: {}
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            //await addActivity(activity); // addActivity is a function that sends the activity data to the server
            // Call the callback function to refresh the activity list
            onActivityAdded(); // This will trigger a reload of the activity list
            // Optionally, reset the form state
            setActivity({ type: "RUNNING", duration: '', caloriesBurned: '' }); // Reset the form state
        } catch (error) {
            console.error(error);
        }
    }

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ mb: 4 }}>
            <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Activity Type</InputLabel>
                <Select
                    value={activity.type}
                    onChange={(e) => setActivity({ ...activity, type: e.target.value })}> // activity.type is a string
                    // The options should match the types defined in your backend
                    <MenuItem value="RUNNING">Running</MenuItem>
                    <MenuItem value="WALKING">Walking</MenuItem>
                    <MenuItem value="CYCLING">Cycling</MenuItem>
                </Select>
            </FormControl>
            <TextField fullWidth
                label="Duration (Minutes)"
                type='number'
                sx={{ mb: 2 }}
                value={activity.duration}
                onChange={(e) => setActivity({ ...activity, duration: e.target.value })} />

            <TextField fullWidth
                label="Calories Burned"
                type='number'
                sx={{ mb: 2 }}
                value={activity.caloriesBurned}
                onChange={(e) => setActivity({ ...activity, caloriesBurned: e.target.value })} />

            <Button type='submit' variant='contained'>
                Add Activity
            </Button>
        </Box>
    )
}

export default ActivityForm