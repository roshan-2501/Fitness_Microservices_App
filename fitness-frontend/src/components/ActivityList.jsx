import { Button, Grid } from '@mui/material';
import React, { useState } from 'react'
import { useNavigate } from 'react-router';
import { getActivities } from '../services/api';

const ActivityList = () => {

    const [activities, setActivities] = useState([]);
    const navigate = useNavigate();

    const fetchActivities = async () => {
        try {
            const response = await getActivities();
            setActivities(response.data);
        } catch (error) {
            if (error.response) {
                // Backend responded with an error status code
                console.error('Backend response error:', error.response.data);
                console.error('Status:', error.response.status);
                console.error('Headers:', error.response.headers);
            } else if (error.request) {
                // Request was made but no response received
                console.error('No response received:', error.request);
            } else {
                // Something else caused the error
                console.error('Error', error.message);
            }
            console.error('Full error object:', error);
        }
    };

    return (
        <Grid container spacing={2}>
            {activities.map((activity) => (
                <Grid item xs={12} sm={6} md={4} key={activity.id}>
                    <div onClick={() => navigate(`/activities/${activity.id}`)}>
                        <h3>{activity.type}</h3>
                        <p>Duration: {activity.duration} minutes</p>
                        <p>Calories Burned: {activity.caloriesBurned}</p>
                    </div>
                </Grid>
            ))}
            <Button variant="contained" onClick={fetchActivities}>Refresh Activities</Button>
        </Grid>
    )
}

export default ActivityList